package com.sptek._frameworkWebCore.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
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

import static org.springframework.http.MediaType.*;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class MessageConverterConfig implements WebMvcConfigurer {
    //jason->object, object->jason

    private final ObjectMapper objectMapper;

    @Bean
    //req,res 단에서 object <-> json 처리하기 위한 MessageConverter
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        List<MediaType> supportedMediaTypes = new ArrayList<>();
        supportedMediaTypes.add(APPLICATION_JSON); //JSON 응답때 적용됨

        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(supportedMediaTypes);
        mappingJackson2HttpMessageConverter.setPrettyPrint(true);
        mappingJackson2HttpMessageConverter.setDefaultCharset(StandardCharsets.UTF_8);
        return mappingJackson2HttpMessageConverter;
    }

    @Override
    //framework에서 사용한 messageConvertor 설정
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        //json 을 위한 매시지 컨버터로 등록
        converters.add(mappingJackson2HttpMessageConverter());

        //그외의 경우 기본용
        List<MediaType> supportedMediaTypes = new ArrayList<>();
        supportedMediaTypes.add(TEXT_HTML); //TEXT_HTML 또는 TEXT_PLAIN 때 적용됨
        supportedMediaTypes.add(TEXT_PLAIN);

        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
        stringHttpMessageConverter.setSupportedMediaTypes(supportedMediaTypes);
        stringHttpMessageConverter.setDefaultCharset(StandardCharsets.UTF_8);
        converters.add(stringHttpMessageConverter);

        WebMvcConfigurer.super.configureMessageConverters(converters);
    }

}
