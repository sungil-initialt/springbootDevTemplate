package com.sptek._frameworkWebCore.base.constant;

import com.sptek._frameworkWebCore.util.SptFwUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

// todo: --> 코드 개선이 필요한지 살펴보자
@Slf4j
public class RequestMappingAnnotationRegister {
    // 컨트롤러 매서드에 적용된 타멧 페키지 어노테이션(클레스와 메소드 모두에 적용된 어노테이션) 정보를 모두 가지고 있는 역할
    private static final String TARGET_ANNOTATION_PACKAGE = "com.sptek.webfw.annotation";

    // 한 번 초기화된 후에 변경 여지가 없기 때문에 속도 측면에서 유리하고 Thread Safe 한 unmodifiableMap을 사용함 (ConcurrentHashMap을 쓰지 않은 이유)
    private static Map<String, Map<String, Map<String, Object>>> requestAnnotationCache = Collections.emptyMap();
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();
    //OPTIONS은 메소드가 없어도 스프링 단에서 처림됨, HEAD는 메소드가 없어도 스프링단에서 GET이 호출됨(단 body를 내리지 않는다)
    private static final List<String> ALL_HTTP_METHODS = Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH"/*, "OPTIONS", "HEAD"*/);

    public RequestMappingAnnotationRegister(ApplicationContext applicationContext) {
        if (!requestAnnotationCache.isEmpty()) {
            return;
        }

        RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        Map<String, Map<String, Map<String, Object>>> tempRequestAnnotationCache = new HashMap<>();
        StringBuilder logBodyForPathEmpty = new StringBuilder();
        StringBuilder logBodyForNonSpecificMapping = new StringBuilder();

        requestMappingHandlerMapping.getHandlerMethods().forEach((requestMappingInfo, handlerMethod) -> {
            Set<String> methods;
            if (requestMappingInfo.getMethodsCondition().getMethods().isEmpty()) {
                methods = new HashSet<>(ALL_HTTP_METHODS);
                logBodyForNonSpecificMapping.append(requestMappingInfo).append("\n");
            } else {
                methods = requestMappingInfo.getMethodsCondition().getMethods().stream().map(Enum::name).collect(Collectors.toSet());
            }

            Set<String> patterns = requestMappingInfo.getPathPatternsCondition() != null ?
                    requestMappingInfo.getPathPatternsCondition().getPatternValues() :
                    requestMappingInfo.getDirectPaths();

            Map<String, Map<String, Object>> annotations = getAnnotationsWithAttributes(handlerMethod);

            if (!patterns.isEmpty()) {
                patterns.forEach(urlPattern -> {
                    methods.forEach(method -> tempRequestAnnotationCache.put(method + ":" + urlPattern, annotations));
                });
            } else {
                logBodyForPathEmpty.append(requestMappingInfo).append("\n");
            }
        });

        requestAnnotationCache = Collections.unmodifiableMap(tempRequestAnnotationCache);
        log.debug(SptFwUtil.convertSystemNotice("Url Mapping Annotation List", requestAnnotationCache.toString()));
        log.info(SptFwUtil.convertSystemNotice("Except Url List (PathPatternsCondition and DirectPaths are both empty)", logBodyForPathEmpty.isEmpty() ? "No List" : logBodyForPathEmpty.toString()));
        log.info(SptFwUtil.convertSystemNotice("Non-specific mapping Check List (it's not recommended)", logBodyForNonSpecificMapping.isEmpty() ? "No List" : logBodyForNonSpecificMapping.toString()));
    }

    /**
     * 핸들러 메소드에서 어노테이션과 속성 정보를 가져옴
     */
    private Map<String, Map<String, Object>> getAnnotationsWithAttributes(HandlerMethod handlerMethod) {
        Map<String, Map<String, Object>> annotationData = new HashMap<>();

        // 클래스에 달린 어노테이션 처리
        for (Annotation annotation : handlerMethod.getBeanType().getAnnotations()) {
            if (annotation.annotationType().getPackageName().startsWith(TARGET_ANNOTATION_PACKAGE)) {
                annotationData.put(annotation.annotationType().getName(), extractAnnotationAttributes(annotation));
            }
        }

        // 메소드에 달린 어노테이션 처리 (메소드 부분을 후 처리 함으로 메소드 적용된 내용이 최종 남게 됨, 메소드 적용이 우선순위가 높음으로..)
        for (Annotation annotation : handlerMethod.getMethod().getAnnotations()) {
            if (annotation.annotationType().getPackageName().startsWith(TARGET_ANNOTATION_PACKAGE)) {
                annotationData.put(annotation.annotationType().getName(), extractAnnotationAttributes(annotation));
            }
        }

        return annotationData;
    }

    /**
     * 어노테이션의 속성 정보를 추출
     */
    private Map<String, Object> extractAnnotationAttributes(Annotation annotation) {
        Map<String, Object> attributes = new HashMap<>();
        Method[] methods = annotation.annotationType().getDeclaredMethods();

        for (Method method : methods) {
            try {
                attributes.put(method.getName(), method.invoke(annotation));
            } catch (Exception e) {
                log.error("Failed to extract attribute '{}' from annotation '{}'", method.getName(), annotation.annotationType().getName(), e);
            }
        }
        return attributes;
    }

    public static boolean hasAnnotation(HttpServletRequest request, Class<? extends Annotation> annotation) {
        return hasAnnotation(request.getMethod() + ":" + request.getRequestURI(), annotation);
    }

    public static boolean hasAnnotation(String methodAndUrl, Class<? extends Annotation> annotation) {
        // 우선 단순 매칭 후, 패턴 매칭
        if (requestAnnotationCache.containsKey(methodAndUrl)) {
            return requestAnnotationCache.get(methodAndUrl).containsKey(annotation.getName());
        } else {
            return requestAnnotationCache.entrySet().stream()
                    .anyMatch(entry -> pathMatcher.match(entry.getKey(), methodAndUrl) && entry.getValue().containsKey(annotation.getName()));
        }
    }

    public static Map<String, Object> getAnnotationAttributes(HttpServletRequest request, Class<? extends Annotation> annotation) {
        return getAnnotationAttributes(request.getMethod() + ":" + request.getRequestURI(), annotation);
    }

    public static Map<String, Object> getAnnotationAttributes(String methodAndUrl, Class<? extends Annotation> annotation) {
        if (requestAnnotationCache.containsKey(methodAndUrl)) {
            return requestAnnotationCache.get(methodAndUrl).getOrDefault(annotation.getName(), Map.of());
        } else {
            return requestAnnotationCache.entrySet().stream()
                    .filter(entry -> pathMatcher.match(entry.getKey(), methodAndUrl) && entry.getValue().containsKey(annotation.getName()))
                    .findFirst()
                    .map(entry -> entry.getValue().getOrDefault(annotation.getName(), Map.of()))
                    .orElse(Map.of());
        }
    }
}