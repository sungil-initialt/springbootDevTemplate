package com.sptek.webfw.config;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sptek.webfw.interceptor.ReqInfoLoggingInterceptor;
import com.sptek.webfw.interceptor.UvLoggingInterceptor;
import com.sptek.webfw.interceptor.XxxInterceptor;
import com.sptek.webfw.support.InterceptorMatchSupport;
import com.sptek.webfw.support.XssProtectSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Configuration
//@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private ReqInfoLoggingInterceptor reqInfoLoggingInterceptor;
    @Autowired
    private UvLoggingInterceptor uvLoggingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //@formatter:off
        String[] swaggerAndErrorExcludePathPatterns = new String[] {
                "/v2/api-docs",
                "/configuration/ui",
                "/configuration/security",
                "/swagger-resources/**",
                "/swagger-ui.html",
                "/webjars/**",
                "/swagger/**",
                "error"
        };

        registry.addInterceptor(this.reqInfoLoggingInterceptor).addPathPatterns("/**").excludePathPatterns(swaggerAndErrorExcludePathPatterns);
        registry.addInterceptor(this.uvLoggingInterceptor).addPathPatterns("/**").excludePathPatterns(swaggerAndErrorExcludePathPatterns);
        registry.addInterceptor(xxxInterceptorMatchSupport()).addPathPatterns("/api").excludePathPatterns(swaggerAndErrorExcludePathPatterns);

        WebMvcConfigurer.super.addInterceptors(registry);
    }

    private HandlerInterceptor xxxInterceptorMatchSupport() {
        final InterceptorMatchSupport interceptorMatchSupport = new InterceptorMatchSupport(new XxxInterceptor());

        // PathMatcherInterceptor를 로그인 인터셉터인 것처럼 인터셉터로 등록한다.
        return interceptorMatchSupport
                .excludePathPattern("/**", HttpMethod.GET)
                .includePathPattern("/inserte/**", HttpMethod.POST)
                .includePathPattern("/update/**", HttpMethod.PUT)
                .excludePathPattern("/delete/**", HttpMethod.DELETE);
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");

        registry.addResourceHandler("/css/**").addResourceLocations("/resources/static/css/");
        registry.addResourceHandler("/images/**").addResourceLocations("/resources/static/images/");
        registry.addResourceHandler("/js/**").addResourceLocations("/resources/static/js/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        //registry.addViewController("/error").setViewName("error");
        registry.addRedirectViewController("/api/demo-ui.html", "/demo-ui.html");
    }

    // application.yml 에서 thymeleaf로 설정
    /*
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.jsp("/WEB-INF/views/", ".jsp");
        WebMvcConfigurer.super.configureViewResolvers(registry);
    }
    */

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setLocale(Locale.KOREA);
        objectMapper.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

        objectMapper.getFactory().setCharacterEscapes(new XssProtectSupport());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return objectMapper;
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {


        List<MediaType> supportedMediaTypes = new ArrayList<>();
        supportedMediaTypes.add(APPLICATION_JSON);

        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper());
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(supportedMediaTypes);
        mappingJackson2HttpMessageConverter.setPrettyPrint(true);
        return mappingJackson2HttpMessageConverter;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
        stringHttpMessageConverter.setDefaultCharset(StandardCharsets.UTF_8);

        converters.add(stringHttpMessageConverter);
        converters.add(mappingJackson2HttpMessageConverter());

        WebMvcConfigurer.super.configureMessageConverters(converters);
    }

    @Bean(name = "multipartResolver")
    public StandardServletMultipartResolver multipartResolver() {
        final StandardServletMultipartResolver multipartResolver = new StandardServletMultipartResolver();
        //multipartResolver.setMaxUploadSize(62914560);			// 60MB * 1024 * 1024
        //multipartResolver.setMaxUploadSizePerFile(10485760);	// 10MB * 1024 * 1024

        return multipartResolver;
    }

}
