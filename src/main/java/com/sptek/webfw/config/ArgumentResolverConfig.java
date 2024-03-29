package com.sptek.webfw.config;


import com.sptek.webfw.argumentResolver.ArgumentResolverForMyUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Slf4j
@Configuration
public class ArgumentResolverConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        //controller에서 request 데이터를 object로 바인딩 해줄때 단순 바인딩이 아니라 HandlerMethodArgumentResolver를 구현한것들이 있으면 그에 따라 처리해줌.
        //HandlerMethodArgumentResolver를 구현해논 객체를 미리 등록해 둔다.
        resolvers.add(new ArgumentResolverForMyUser());
        WebMvcConfigurer.super.addArgumentResolvers(resolvers);
    }
}
