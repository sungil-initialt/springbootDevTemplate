package com.sptek.webfw.base.constant;

import com.sptek.webfw.util.SptFwUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class OLD_RequestMappingAnnotationRegister {
    // 해당 request 에 uri 와 매핑된 컨틀로러 매서드에 적용된 어노테이션(클레스와 메소드 모두에 적용된 어노테이션) 정보를 모두 가지고 있는 역할을 함

    // 한 번 초기화된 후에 변경 여지가 없기 때문에 속도 측면에서 유리하고 Thread Safe 한 unmodifiableMap을 사용함 (ConcurrentHashMap을 쓰지 않은 이유)
    private static Map<String, Set<String>> requestAnnotationCache = Collections.emptyMap();
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();
    //OPTIONS은 메소드가 없어도 스프링 단에서 처림됨, HEAD는 메소드가 없어도 스프링단에서 GET이 호출됨(단 body를 내리지 않는다)
    private static final List<String> ALL_HTTP_METHODS = Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH"/*, "OPTIONS", "HEAD"*/);

    public OLD_RequestMappingAnnotationRegister(ApplicationContext applicationContext) {
        if (!requestAnnotationCache.isEmpty()) {
            return;
        }

        RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        Map<String, Set<String>> tempCache = new HashMap<>();
        StringBuilder logBodyForPathEmpty = new StringBuilder();
        StringBuilder logBodyForNotRecommended = new StringBuilder();

        requestMappingHandlerMapping.getHandlerMethods().forEach((requestMappingInfo, handlerMethod) -> {

            Set<String> methods;
            if (requestMappingInfo.getMethodsCondition().getMethods().isEmpty()) {
                methods = new HashSet<>(ALL_HTTP_METHODS);
                logBodyForNotRecommended.append(requestMappingInfo).append("\n");
            } else {
                methods = requestMappingInfo.getMethodsCondition().getMethods().stream().map(Enum::name).collect(Collectors.toSet());
            }

            Set<String> patterns = requestMappingInfo.getPathPatternsCondition() != null ?
                    requestMappingInfo.getPathPatternsCondition().getPatternValues() :
                    requestMappingInfo.getDirectPaths();

            Set<String> annotations = getAllAnnotationsFromHandlerMethod(handlerMethod);

            if (!patterns.isEmpty()) {
                patterns.forEach(urlPattern -> {
                    methods.forEach(method -> tempCache.put(method + ":" + urlPattern, annotations));
                });
            } else {
                logBodyForPathEmpty.append(requestMappingInfo).append("\n");
            }
        });

        requestAnnotationCache = Collections.unmodifiableMap(tempCache);
        log.debug(SptFwUtil.convertSystemNotice("Url Mapping Annotation List", requestAnnotationCache.toString()));
        log.info(SptFwUtil.convertSystemNotice("Except Url List (PathPatternsCondition and DirectPaths are both empty)", logBodyForPathEmpty.isEmpty() ? "No List" : logBodyForPathEmpty.toString()));
        log.info(SptFwUtil.convertSystemNotice("Non-specific mapping Check List (it's not recommended)", logBodyForNotRecommended.isEmpty() ? "No List" : logBodyForNotRecommended.toString()));
    }

    private Set<String> getAllAnnotationsFromHandlerMethod(HandlerMethod handlerMethod) {
        // 메소드 어노테이션 추가
        Set<String> methodAnnotations = Arrays.stream(handlerMethod.getMethod().getAnnotations())
                .map(annotation -> annotation.annotationType().getName())
                .collect(Collectors.toSet());

        // 클래스 어노테이션 추가
        Set<String> classAnnotations = Arrays.stream(handlerMethod.getBeanType().getAnnotations())
                .map(annotation -> annotation.annotationType().getName())
                .collect(Collectors.toSet());

        // 메소드와 클래스 어노테이션 병합
        methodAnnotations.addAll(classAnnotations);
        return methodAnnotations;
    }

    public static boolean hasAnnotation(HttpServletRequest request, Class<? extends Annotation> annotation) {
        return hasAnnotation(request.getMethod() + ":" + request.getRequestURI(), annotation);
    }

    public static boolean hasAnnotation(String methodAndUrl, Class<? extends Annotation> annotation) {
        //대부분의 경우 패턴 적용이 아니기 때문에 검색 속도 측면 에서 우선 단순 검색 후 없는 경우에 다시 패턴 검색을 하는 방식으로 처리함.
        if (requestAnnotationCache.getOrDefault(methodAndUrl, Set.of()).contains(annotation.getName())) {
            return true;
        } else {
            return requestAnnotationCache.entrySet().stream()
                    .anyMatch(entry -> pathMatcher.match(entry.getKey(), methodAndUrl) && entry.getValue().contains(annotation.getName()));
        }
    }

}
