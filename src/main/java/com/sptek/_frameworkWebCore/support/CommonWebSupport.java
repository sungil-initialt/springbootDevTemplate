package com.sptek._frameworkWebCore.support;

import com.sptek._frameworkWebCore.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.util.Locale;
import java.util.Optional;

/*
모든 Controller는 해당 클레스를 상속받아 구현함.
Controller에서 이용할수 있는 공통 기능을 추가해 나가면 됨
 */
@Slf4j
public class CommonWebSupport {

    @Autowired // todo : final로 생성자 주입 받으면 에러나는 이유 확인 필요
    private MessageSource messageSource;

    //현재 Locale에 해당하는 메시지로 제공한다.
    public String getI18nMessage(String code, @Nullable Object[] args) {
        String langCode;
        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(getCurrenRequest());

        if (localeResolver == null) {
            langCode = Optional.ofNullable(CookieUtil.getCookies("lang"))
                    .filter(list -> !list.isEmpty())
                    .map(list -> list.get(0).getValue())
                    .orElse(Locale.getDefault().getLanguage());
        } else {
            langCode = LocaleContextHolder.getLocale().toLanguageTag();
        }

        return messageSource.getMessage(code, args, Locale.forLanguageTag(langCode));
    }

    public String getCurLangTag(){
        return LocaleContextHolder.getLocale().toLanguageTag();
    }

    private HttpServletRequest getCurrenRequest(){
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }

    private HttpServletResponse getCurrenResponse(){
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
    }
}
