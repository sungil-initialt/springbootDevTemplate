package com.sptek.webfw.base.constant;

import com.sptek.webfw.util.SptFwUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class RequestMappingAnnotationRegister {
    // 해당 request 에 uri 와 매핑된 컨틀로러 매서드에 적용된 어노테이션(클레스와 메소드 모두에 적용된 어노테이션) 정보를 모두 가지고 있는 역할을 함

    // 한 번 초기화된 후에 변경 여지가 없기 때문에 속도 측면에서 유리하고 Thread Safe 한 unmodifiableMap을 사용함 (ConcurrentHashMap을 쓰지 않은 이유)
    private static Map<String, Set<String>> requestAnnotationCache= Collections.emptyMap();
    private static AntPathMatcher pathMatcher = new AntPathMatcher();

    public RequestMappingAnnotationRegister(ApplicationContext applicationContext) {
        if(!requestAnnotationCache.isEmpty()) {
            return;
        }

        RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        Map<String, Set<String>> tempCache = new HashMap<>();
        StringBuffer logBody = new StringBuffer();

        requestMappingHandlerMapping.getHandlerMethods().forEach((requestMappingInfo, handlerMethod) -> {
            Set<String> patterns = requestMappingInfo.getPathPatternsCondition() != null ?
                    requestMappingInfo.getPathPatternsCondition().getPatternValues() :
                    requestMappingInfo.getDirectPaths();

            Set<String> annotations = getAllAnnotations(handlerMethod);

            if (!patterns.isEmpty()) {
                patterns.forEach(urlPattern -> tempCache.put(urlPattern, annotations));
            } else {
                logBody.append(requestMappingInfo).append("\n");
            }
        });

        requestAnnotationCache = Collections.unmodifiableMap(tempCache);
        log.info(SptFwUtil.convertSystemNotice("Url Mapping Annotation List", requestAnnotationCache.toString()));
        log.warn(SptFwUtil.convertSystemNotice("Except Url List (PathPatternsCondition and DirectPaths are both empty)", logBody.isEmpty()? new StringBuffer("No Except Url") : logBody));
    }

    private Set<String> getAllAnnotations(HandlerMethod handlerMethod) {
        // 메소드 어노테이션 추가
        Set<String> methodAnnotations = Set.of(handlerMethod.getMethod().getAnnotations())
                .stream()
                .map(annotation -> annotation.annotationType().getName())
                .collect(Collectors.toSet());

        // 클래스 어노테이션 추가
        Set<String> classAnnotations = Set.of(handlerMethod.getBeanType().getAnnotations())
                .stream()
                .map(annotation -> annotation.annotationType().getName())
                .collect(Collectors.toSet());

        // 메소드와 클래스 어노테이션 병합
        methodAnnotations.addAll(classAnnotations);
        return methodAnnotations;
    }

    private static Set<String> getAnnotations(String url) {
        return requestAnnotationCache.entrySet().stream()
                .filter(entry -> pathMatcher.match(entry.getKey(), url))
                .flatMap(entry -> entry.getValue().stream())
                .collect(Collectors.toSet());
    }

    public static boolean hasAnnotation(HttpServletRequest request, Class<? extends Annotation> annotation) {
        return hasAnnotation(request.getRequestURI(), annotation);
    }

    //대부분의 경우 url 매핑이 패턴 방식이 아니기 때문에 단순 매칭 검색 후 없는 경우 에만 다시한번 패턴 매칭검색을 진행함(성능상 이점)
    public static boolean hasAnnotation(String uri, Class<? extends Annotation> annotation) {
        if(!requestAnnotationCache.getOrDefault(uri, Set.of()).contains(annotation.getName())) {
            return hasAnnotationWithPattern(uri, annotation);
        } else {
            return true;
        }
    }

    private static boolean hasAnnotationWithPattern(String uri, Class<? extends Annotation> annotation) {
        return requestAnnotationCache.entrySet().stream()
                .anyMatch(entry -> pathMatcher.match(entry.getKey(), uri) && entry.getValue().contains(annotation.getName()));
    }


}
