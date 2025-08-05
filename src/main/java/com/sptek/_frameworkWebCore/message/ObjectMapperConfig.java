package com.sptek._frameworkWebCore.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sptek._frameworkWebCore.annotation.Enable_XssProtectForApi_At_Main;
import com.sptek._frameworkWebCore.annotation.annotationCondition.HasAnnotationOnMain_At_Bean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;
import java.util.TimeZone;

@Slf4j
@Configuration
public class ObjectMapperConfig {
    //jason->object, object->jason
    //Locale 과 TimeZone 은 system default로 설정함 (user 별 변환이 필요시 해당 user의 locale 을 이용해서 변환된 string 값을 내리도록 할것)

    @Bean
    // @Enable_XssProtectForApi_At_ControllerMethod를 통해 선별적 xss 처리, 더 권장?
    @HasAnnotationOnMain_At_Bean(value = Enable_XssProtectForApi_At_Main.class, negate = true)
    public ObjectMapper objectMapperWithoutXssProtectHelper() {
        //locale, timeZone등 공통요소에 대한 setting을 할수 있다.
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setLocale(Locale.getDefault());
        objectMapper.setTimeZone(TimeZone.getDefault());
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); //null 값은 json에서 제외
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    @Bean
    @HasAnnotationOnMain_At_Bean(value = Enable_XssProtectForApi_At_Main.class, negate = false)
    public ObjectMapper objectMapperWithXssProtectHelper() {
        //locale, timeZone등 공통요소에 대한 setting을 할수 있다.
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setLocale(Locale.getDefault());
        objectMapper.setTimeZone(TimeZone.getDefault());
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); //null 값은 json에서 제외
        objectMapper.getFactory().setCharacterEscapes(new XssProtectHelper()); //Xss 방지 적용
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }
}
