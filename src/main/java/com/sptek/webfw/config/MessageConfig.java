package com.sptek.webfw.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sptek.webfw.support.XssProtectSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@Configuration
public class MessageConfig implements WebMvcConfigurer {

    @Bean
    public ObjectMapper objectMapper() {
        //MessageConverter 에 활용하기 위한 objectMapper를 생성, locale, timeZone등 공통요소에 대한 setting을 할수 있다.

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setLocale(Locale.KOREA);
        objectMapper.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

        objectMapper.getFactory().setCharacterEscapes(new XssProtectSupport()); //Xss 방지 적용
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return objectMapper;
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        //req, res 에서 message Convert로 활용하기 위한 Convertor.

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
        //framework에서 default로 사용한 messageConvertor 설정

        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
        stringHttpMessageConverter.setDefaultCharset(StandardCharsets.UTF_8);

        converters.add(stringHttpMessageConverter);
        converters.add(mappingJackson2HttpMessageConverter());

        WebMvcConfigurer.super.configureMessageConverters(converters);
    }

}
