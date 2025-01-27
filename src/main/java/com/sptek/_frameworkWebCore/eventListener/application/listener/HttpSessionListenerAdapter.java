package com.sptek._frameworkWebCore.eventListener.application.listener;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class HttpSessionListenerAdapter {

    private final ApplicationEventPublisher applicationEventPublisher;

    //@Override 에 직접 원하는 처리가 가능하지만 다른 eventLitener 와 사용 방식 통일을 마추기 위해서 Adapter 크래스를 중간에 구현함
    @Bean
    public HttpSessionListener httpSessionListener() {
        return new HttpSessionListener() {
            @Override
            public void sessionCreated(HttpSessionEvent httpSessionEvent) {
                applicationEventPublisher.publishEvent(new HttpSessionCreatedEventAdapter(httpSessionEvent));
            }

            @Override
            public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
                applicationEventPublisher.publishEvent(new HttpSessionDestroyedEventAdapter(httpSessionEvent));
            }
        };
    }


    //session 의 Created / Destroyed 상태를 분리한 메시지 만들기 위한 EventAdapter 클레스 임
    @RequiredArgsConstructor
    public class HttpSessionCreatedEventAdapter {
        public final HttpSessionEvent httpSessionEvent;
    }
    @RequiredArgsConstructor
    public class HttpSessionDestroyedEventAdapter {
        public final HttpSessionEvent httpSessionEvent;
    }
}
