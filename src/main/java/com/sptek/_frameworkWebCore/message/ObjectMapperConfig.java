package com.sptek._frameworkWebCore.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sptek._frameworkWebCore.annotation.Enable_XssProtectForApi_At_Main;
import com.sptek._frameworkWebCore.annotation.annotationCondition.HasAnnotationOnMain_At_Bean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.TimeZone;

@Slf4j
@Configuration
public class ObjectMapperConfig {
    //jason->object, object->jason

    @Bean
    // @Enable_XssProtectForApi_At_ControllerMethod를 통해 선별적 xss 처리, 더 권장?
    @HasAnnotationOnMain_At_Bean(value = Enable_XssProtectForApi_At_Main.class, negate = true)
    public ObjectMapper objectMapperWithoutXssProtectHelper() {
        //locale, timeZone등 공통요소에 대한 setting을 할수 있다.
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setLocale(LocaleContextHolder.getLocale());
        objectMapper.setTimeZone(TimeZone.getTimeZone("Asia/Seoul")); // todo : timezone 에 따른 시간정보 오류 수정 해야함
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); //null 값은 json에서 제외
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    @Bean
    @HasAnnotationOnMain_At_Bean(value = Enable_XssProtectForApi_At_Main.class, negate = false)
    public ObjectMapper objectMapperWithXssProtectHelper() {
        //locale, timeZone등 공통요소에 대한 setting을 할수 있다.
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setLocale(LocaleContextHolder.getLocale());
        objectMapper.setTimeZone(TimeZone.getTimeZone("Asia/Seoul")); // todo : timezone 에 따른 시간정보 오류 수정 해야함
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); //null 값은 json에서 제외
        objectMapper.getFactory().setCharacterEscapes(new XssProtectHelper()); //Xss 방지 적용
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }
}
