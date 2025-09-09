package com.sptek._frameworkWebCore.message;

import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import com.sptek._frameworkWebCore.interceptor.CustomLocaleChangeInterceptor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import java.time.Duration;
import java.util.Locale;
import java.util.TimeZone;

@Configuration
public class LocaleConfig implements WebMvcConfigurer {

    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver(CommonConstants.LOCALE_COOKIE_NAME);
        // 서버 기준으로 디폴트
        cookieLocaleResolver.setDefaultLocale(Locale.getDefault());
        cookieLocaleResolver.setDefaultTimeZone(TimeZone.getDefault());
        cookieLocaleResolver.setCookieMaxAge(Duration.ofDays(CommonConstants.LOCALE_COOKIE_MAX_AGE_DAY));
        cookieLocaleResolver.setCookieHttpOnly(true);
        return cookieLocaleResolver;
    }

    @Bean
    public CustomLocaleChangeInterceptor localeChangeInterceptor() {
        CustomLocaleChangeInterceptor customLocaleChangeInterceptor = new CustomLocaleChangeInterceptor();
        //해당 이름으로 쿼리가 내려가면 해당 값으로 쿠키가 내려가며 동시에 locale 값으로 세팅됨
        customLocaleChangeInterceptor.setParamName(CommonConstants.LOCALE_COOKIE_NAME);
        return customLocaleChangeInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry interceptorRegistry) {
        interceptorRegistry.addInterceptor(localeChangeInterceptor());
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource = new ReloadableResourceBundleMessageSource();
        reloadableResourceBundleMessageSource.setBasename("classpath:/_projectCommonResources/i18n/messages");
        reloadableResourceBundleMessageSource.setDefaultEncoding("UTF-8");
        reloadableResourceBundleMessageSource.setCacheSeconds(60*10); // 리로드 시간
        return reloadableResourceBundleMessageSource;
    }
}
