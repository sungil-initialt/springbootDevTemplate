package com.sptek._frameworkWebCore.config.message;

import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import com.sptek._frameworkWebCore.config.interceptor.CustomLocaleChangeInterceptor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.LocaleResolver;

import java.time.Duration;
import java.util.Locale;

@Configuration
public class LocaleConfig implements WebMvcConfigurer {

    @Bean
    public LocaleResolver localeResolver() {
        //로케일 정보 저장 방식을 쿠키 방식 으로 선택함(쿠키 방식으로 선택하였으나 세션 방식으로도 구현가능)
        CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
        cookieLocaleResolver.setDefaultLocale(Locale.getDefault()); //local 의 기본(개인별 설정이 안된 경우) 설정은 서버의 로케일을 따름

        //어떤 쿠키네임을 사용할지 명시하지 않아도 됨, 디폴트 명은 org.springframework.web.servlet.i18n.CookieLocaleResolver.LOCALE (현재 deprecated 되어 있음)
        cookieLocaleResolver.setCookieName(CommonConstants.LOCALE_NAME); //해당 이름으로 쿠키가 올라오면 해당 값으로 locale을 자동 설정함
        cookieLocaleResolver.setCookieMaxAge(Duration.ofSeconds(CommonConstants.LOCALE_COOKIE_MAX_AGE_SEC)); //비 설정시 session 으로 적용됨

        return cookieLocaleResolver;
    }

    // 기본적으로 제공되는 인터셉터이나.. locale(언어, 국가) 과 동시에 timeZone 설정을 동시에 하기 위해 기능을 수정한 CustomLocaleChangeInterceptor 로 대체함.
    // @Bean
    // public LocaleChangeInterceptor localeChangeInterceptor() {
    //     //로케일 정보를 변경하기 위해서는 요청url?local=kr(언어코드) 형태로 요청 하면 되며 한번 요청 되면 쿠키로 설정 되어 삭제 또는 변경 되기 까지 유지됨
    //     LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
    //     localeChangeInterceptor.setParamName("locale");
    //     return localeChangeInterceptor;
    // }

    @Bean
    public CustomLocaleChangeInterceptor localeChangeInterceptor() {
        CustomLocaleChangeInterceptor localeChangeInterceptor = new CustomLocaleChangeInterceptor();
        localeChangeInterceptor.setParamName(CommonConstants.LOCALE_NAME); //해당 이름으로 url 파람이 전달되면 해당 값으로 쿠키가 내려가며 동시에 locale 값으로 세팅됨
        return localeChangeInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry interceptorRegistry) {
        interceptorRegistry.addInterceptor(localeChangeInterceptor());
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource = new ReloadableResourceBundleMessageSource();
        reloadableResourceBundleMessageSource.setBasename("classpath:/i18n/messages");
        reloadableResourceBundleMessageSource.setDefaultEncoding("UTF-8");
        reloadableResourceBundleMessageSource.setCacheSeconds(60*10); // 리로드 시간
        return reloadableResourceBundleMessageSource;
    }

}
