package com.sptek.webfw.eventListener.application.listener;

import com.sptek.webfw.anotation.*;
import com.sptek.webfw.base.constant.RequestMappingAnnotationRegister;
import com.sptek.webfw.util.SptFwUtil;
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
        log.info(SptFwUtil.convertSystemNotice("UrlMapping Annotation Register", "created"));

        log.debug("test : expect:true, result:{}", RequestMappingAnnotationRegister.hasAnnotation("GET:/api/v1/public/anyone/butNeedControllAuth", EnableApiCommonSuccessResponse.class));
        log.debug("test : expect:false, result:{}", RequestMappingAnnotationRegister.hasAnnotation("GET:/api/v1/public/anyone/butNeedControllAuth", EnableApplicationCommonErrorResponse.class));
        log.debug("test : expect:false, result:{}", RequestMappingAnnotationRegister.hasAnnotation("GET:/auth/user/info/sungilry@naver.com", EnableApiCommonSuccessResponse.class));
        log.debug("test : expect:true, result:{}", RequestMappingAnnotationRegister.hasAnnotation("GET:/auth/user/info/sungilry@naver.com", EnableViewCommonErrorResponse.class));
        log.debug("test : expect:true, result:{}", RequestMappingAnnotationRegister.hasAnnotation("GET:/api/v1/httpCache", EnableRequestDeduplication.class));
        log.debug("test : expect:true, result:{}", RequestMappingAnnotationRegister.hasAnnotation("POST:/api/v1/httpCache", UniversalAnnotationForTest.class));
        log.debug("test : expect:true, result:{}", RequestMappingAnnotationRegister.hasAnnotation("POST:/api/v1/httpCache2", UniversalAnnotationForTest.class));
    }
}
