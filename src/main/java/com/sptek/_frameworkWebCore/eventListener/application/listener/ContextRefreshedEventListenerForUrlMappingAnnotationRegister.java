package com.sptek._frameworkWebCore.eventListener.application.listener;

import com.sptek._frameworkWebCore.annotation.*;
import com.sptek._frameworkWebCore.base.constant.RequestMappingAnnotationRegister;
import com.sptek._frameworkWebCore.util.SptFwUtil;
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

        log.debug("test : expect:true, result:{}", RequestMappingAnnotationRegister.hasAnnotation("GET:/api/v1/anyone/butNeedControllAuth", EnableResponseOfApiCommonSuccess_InRestController.class));
        log.debug("test : expect:false, result:{}", RequestMappingAnnotationRegister.hasAnnotation("GET:/api/v1/anyone/butNeedControllAuth", EnableResponseOfApplicationGlobalException_InMain.class));
        log.debug("test : expect:false, result:{}", RequestMappingAnnotationRegister.hasAnnotation("GET:/auth/user/info/sungilry@naver.com", EnableResponseOfApiCommonSuccess_InRestController.class));
        log.debug("test : expect:true, result:{}", RequestMappingAnnotationRegister.hasAnnotation("GET:/auth/user/info/sungilry@naver.com", EnableResponseOfViewGlobalException_InViewController.class));
        log.debug("test : expect:true, result:{}", RequestMappingAnnotationRegister.hasAnnotation("GET:/api/v1/httpCache", EnableDeduplicationRequest_InRestController_RestControllerMethod.class));
        log.debug("test : expect:true, result:{}", RequestMappingAnnotationRegister.hasAnnotation("POST:/api/v1/httpCache", TestAnnotation_InAll.class));
        log.debug("test : expect:true, result:{}", RequestMappingAnnotationRegister.hasAnnotation("POST:/api/v1/httpCache2", TestAnnotation_InAll.class));
    }
}
