package com.sptek._frameworkWebCore.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sptek._frameworkWebCore.annotation.EnableXssProtectorForApi_InMain;
import com.sptek._frameworkWebCore.annotation.annotationCondition.HasAnnotationOnMain_InBean;
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
    @HasAnnotationOnMain_InBean(value = EnableXssProtectorForApi_InMain.class, negate = true)
    //코드 내에서 object <-> json 처리하기 위한 독립적 형태로도 사용될 수 있으며 req, res 단에서 사용할 MessageConverter 의 base 로 활용
    public ObjectMapper objectMapperWithoutXssProtectHelper() {
        //locale, timeZone등 공통요소에 대한 setting을 할수 있다.
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setLocale(LocaleContextHolder.getLocale());
        objectMapper.setTimeZone(TimeZone.getTimeZone("Asia/Seoul")); // todo : timezone 에 따른 시간정보 오류 수정 해야함
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); //null 값은 json에서 제외
        //objectMapper.getFactory().setCharacterEscapes(new XssProtectHelper()); //Xss 방지 적용
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    @Bean
    @HasAnnotationOnMain_InBean(value = EnableXssProtectorForApi_InMain.class, negate = false)
    //코드 내에서 object <-> json 처리하기 위한 독립적 형태로도 사용될 수 있으며 req, res 단에서 사용할 MessageConverter 의 base 로 활용
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
