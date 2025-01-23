package com.sptek.webfw.eventListener.application.listener;

import com.sptek.webfw.base.constant.RequestMappingAnnotationRegister;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ContextRefreshedEventListenerForUrlMappingAnnotationRegister {
    @EventListener
    public void contextRefreshedEvent(ContextRefreshedEvent contextRefreshedEvent) {
        //시스템 로딩이 완료된 시점에 UrlMappingAnnotationRegistor 를 생성해 준다.
        new RequestMappingAnnotationRegister(contextRefreshedEvent.getApplicationContext());
        log.info("UrlMappingAnnotationRegister created.");

        // log.debug("test : /api/v1/public/anyone/butNeedControllAuth - EnableApiCommonSuccessResponse ({})", RequestMappingAnnotationRegister.hasAnnotation("/api/v1/public/anyone/butNeedControllAuth", EnableApiCommonSuccessResponse.class));
        // log.debug("test : /api/v1/public/anyone/butNeedControllAuth - EnableApplicationCommonErrorResponse ({})", RequestMappingAnnotationRegister.hasAnnotation("/api/v1/public/anyone/butNeedControllAuth", EnableApplicationCommonErrorResponse.class));
        // log.debug("test : /auth/user/info/sungilry@naver.com - EnableApiCommonSuccessResponse ({})", RequestMappingAnnotationRegister.hasAnnotation("/auth/user/info/sungilry@naver.com", EnableApiCommonSuccessResponse.class));
        // log.debug("test : /auth/user/info/sungilry@naver.com - EnableViewCommonErrorResponse ({})", RequestMappingAnnotationRegister.hasAnnotation("/auth/user/info/sungilry@naver.com", EnableViewCommonErrorResponse.class));
    }
}
