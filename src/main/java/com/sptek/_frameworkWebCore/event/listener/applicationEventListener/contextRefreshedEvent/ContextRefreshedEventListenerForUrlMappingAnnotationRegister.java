package com.sptek._frameworkWebCore.event.listener.applicationEventListener.contextRefreshedEvent;

import com.sptek._frameworkWebCore.base.constant.RequestMappingAnnotationRegister;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ContextRefreshedEventListenerForUrlMappingAnnotationRegister {
    @EventListener
    public void listen(ContextRefreshedEvent contextRefreshedEvent) throws Exception {
        log.debug("Event!");
        //시스템 로딩이 완료된 시점에 UrlMappingAnnotationRegistor 를 생성해 준다.
        new RequestMappingAnnotationRegister(contextRefreshedEvent.getApplicationContext());

//        log.debug("test : expect:true, result:{}", RequestMappingAnnotationRegister.hasAnnotation("GET:/api/v1/anyone/butNeedControllAuth", Enable_ResponseOfApiCommonSuccess_At_RestController.class));
//        log.debug("test : expect:false, result:{}", RequestMappingAnnotationRegister.hasAnnotation("GET:/api/v1/anyone/butNeedControllAuth", Enable_ResponseOfApplicationGlobalException_At_Main.class));
//        log.debug("test : expect:false, result:{}", RequestMappingAnnotationRegister.hasAnnotation("GET:/auth/user/info/sungilry@naver.com", Enable_ResponseOfApiCommonSuccess_At_RestController.class));
//        log.debug("test : expect:true, result:{}", RequestMappingAnnotationRegister.hasAnnotation("GET:/auth/user/info/sungilry@naver.com", Enable_ResponseOfViewGlobalException_At_ViewController.class));
//        log.debug("test : expect:true, result:{}", RequestMappingAnnotationRegister.hasAnnotation("GET:/api/v1/httpCache", Enable_PreventDuplicateRequest_At_RestController_RestControllerMethod.class));
//        log.debug("test : expect:true, result:{}", RequestMappingAnnotationRegister.hasAnnotation("POST:/api/v1/httpCache", TestAnnotation_At_All.class));
//        log.debug("test : expect:true, result:{}", RequestMappingAnnotationRegister.hasAnnotation("POST:/api/v1/httpCache2", TestAnnotation_At_All.class));
    }
}
