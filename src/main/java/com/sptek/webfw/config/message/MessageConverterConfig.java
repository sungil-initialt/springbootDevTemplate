package com.sptek.webfw.config.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sptek.webfw.support.XssProtectSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
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
import static org.springframework.http.MediaType.TEXT_HTML;
import static org.springframework.http.MediaType.TEXT_PLAIN;

@Slf4j
@Configuration
public class MessageConverterConfig implements WebMvcConfigurer {
    //jason->object, object->jason

    @Bean
    //코드 내에서 object <-> json 처리하기 위한 독립적 형태로도 사용될 수 있으며 req, res 단에서 사용할 MessageConverter 의 base 로 활용
    public ObjectMapper objectMapper() {
        //locale, timeZone등 공통요소에 대한 setting을 할수 있다.
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setLocale(LocaleContextHolder.getLocale());
        objectMapper.setTimeZone(TimeZone.getTimeZone("Asia/Seoul")); // todo : timezone 에 따른 시간정보 오류 수정 해야함
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); //null 값은 json에서 제외
        objectMapper.getFactory().setCharacterEscapes(new XssProtectSupport()); //Xss 방지 적용
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return objectMapper;
    }

    @Bean
    //req,res 단에서 object <-> json 처리하기 위한 MessageConverter
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        List<MediaType> supportedMediaTypes = new ArrayList<>();
        supportedMediaTypes.add(APPLICATION_JSON); //JSON 응답때 적용됨

        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper());
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
