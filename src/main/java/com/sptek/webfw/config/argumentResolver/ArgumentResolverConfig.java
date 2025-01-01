package com.sptek.webfw.config.argumentResolver;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Slf4j
@Configuration
public class ArgumentResolverConfig implements WebMvcConfigurer {
// controller에서 request 데이터를 object로 바인딩 해줄때 단순 바인딩이 아니라 HandlerMethodArgumentResolver를 구현한것들이 있으면 그에 따라 처리해줌.

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        //구현한 ArgumentResolver를 등록해 준다.
        resolvers.add(new ExampleArgumentResolverForMyUserDto());

        WebMvcConfigurer.super.addArgumentResolvers(resolvers);
    }
}
