package com.sptek._frameworkWebCore.util.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextListener;

@Configuration
public class UtilBeans {

    @Bean
    public RequestContextListener requestContextListener() {
        // 중요!: 현재 요청(HttpServletRequest)을 RequestContextHolder 에 바인딩 하는 역할을 함, config 성격 으로 추후 변경할 내용 없음
        // 현재 스레드 에서 요청 정보를 전역적 으로 사용 가능, SpringUtil 클레스 를 사용 하기 위해 반드시 필요함
        return new RequestContextListener();
    }
}
