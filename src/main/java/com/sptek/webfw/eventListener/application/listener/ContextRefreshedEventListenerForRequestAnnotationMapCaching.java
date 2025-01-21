package com.sptek.webfw.eventListener.application.listener;

import com.sptek.webfw.util.SptFwUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

//--> 여기 정리 해야함
@Slf4j
@Component
public class ContextRefreshedEventListenerForRequestAnnotationMapCaching {
    //한번 초기화된 후에 변경 여지가 없기 때문에 속도측면에서 유리하고 Thread Safe 한 unmodifiableMap 을 사용함 (ConcurrentHashMap 을 쓰지 않은 이유)
    private Map<String, Set<String>> requestAnnotationCache = Collections.emptyMap();

    @EventListener
    public void contextRefreshedEvent(ContextRefreshedEvent contextRefreshedEvent) {
        RequestMappingHandlerMapping requestMappingHandlerMapping = contextRefreshedEvent.getApplicationContext().getBean(RequestMappingHandlerMapping.class);
        Map<String, Set<String>> tempCache = new HashMap<>();

        requestMappingHandlerMapping.getHandlerMethods().forEach((requestMappingInfo, handlerMethod) -> {
            Set<String> annotations = getAllAnnotations(handlerMethod);
            Set<String> patterns = requestMappingInfo.getPatternsCondition() != null ?
                    requestMappingInfo.getPatternsCondition().getPatterns() :
                    requestMappingInfo.getDirectPaths();

            if (!patterns.isEmpty()) {
                patterns.forEach(urlPattern -> tempCache.put(urlPattern, annotations));
            } else {
                log.warn("PatternsCondition and DirectPaths are both null or empty for request mapping info: {}", requestMappingInfo);
            }
        });

        requestAnnotationCache = Collections.unmodifiableMap(tempCache);
        log.info(SptFwUtil.convertSystemNotice("Request Annotation MapCaching List", requestAnnotationCache.toString()));
    }

    private Set<String> getAllAnnotations(HandlerMethod handlerMethod) {
        // Add method annotations
        Set<String> methodAnnotations = Set.of(handlerMethod.getMethod().getAnnotations())
                .stream()
                .map(annotation -> annotation.annotationType().getName())
                .collect(Collectors.toSet());

        // Add class annotations
        Set<String> classAnnotations = Set.of(handlerMethod.getBeanType().getAnnotations())
                .stream()
                .map(annotation -> annotation.annotationType().getName())
                .collect(Collectors.toSet());

        // Merge method and class annotations
        methodAnnotations.addAll(classAnnotations);
        return methodAnnotations;
    }

    public boolean hasAnnotation(String url, String annotationClassName) {
        return requestAnnotationCache.getOrDefault(url, Set.of()).contains(annotationClassName);
    }
}
