package com.sptek.webfw.config.argumentResolver;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ArgumentResolverConfig implements WebMvcConfigurer {
// controller에서 request 데이터를 object로 바인딩 해줄때 단순 바인딩이 아니라 HandlerMethodArgumentResolver를 구현한것들이 있으면 그에 따라 처리해줌.

    // 일일이 HandlerMethodArgumentResolver를 등록하던 방식에서 더 나은 방식으로 변경함.
    //@Override
    //public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    //    //구현한 ArgumentResolver를 등록해 준다.
    //    resolvers.add(new ArgumentResolverForMyUserDto());
    //    WebMvcConfigurer.super.addArgumentResolvers(resolvers);
    //}

    private final ApplicationContext applicationContext;

    @Override
    // Spring 컨텍스트에서 모든 HandlerMethodArgumentResolver 빈을 검색하여 자동 등록 하는 방식 으로 변경
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> HandlerMethodArgumentResolvers) {
        HandlerMethodArgumentResolvers.addAll(applicationContext.getBeansOfType(HandlerMethodArgumentResolver.class).values());
    }
}
