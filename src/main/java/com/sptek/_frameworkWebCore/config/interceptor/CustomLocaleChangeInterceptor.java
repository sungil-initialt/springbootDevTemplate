package com.sptek._frameworkWebCore.config.interceptor;

import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import com.sptek._frameworkWebCore.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CustomLocaleChangeInterceptor extends LocaleChangeInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        // locale 처리
        String localeParam = request.getParameter(getParamName());
        if (localeParam != null) {
            Locale locale = parseLocaleValue(localeParam);
            LocaleContextHolder.setLocale(locale);

            // LocaleContextHolder 는 해당 request 에 대한 locale 정보를 갖는 반면 LocaleResolver 는 에플르케이션 레벨에서 정보를 관리함(spring 내부적으로 활용되는 부분이 있음으로 같이 셋팅)
            LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
            if (localeResolver != null) {
                localeResolver.setLocale(request, response, locale);
            }
        }

        // locale 처리시 timezone 처리도 함께 함
        String timeZoneParam = request.getParameter(CommonConstants.TIMEZONE_NAME);
        if (timeZoneParam != null) {
            TimeZone timeZone = TimeZone.getTimeZone(timeZoneParam);
            LocaleContextHolder.setTimeZone(timeZone);
            CookieUtil.createCookieAndAdd(CommonConstants.TIMEZONE_NAME, timeZoneParam, CommonConstants.LOCALE_COOKIE_MAX_AGE_SEC);

        } else {
            List<Cookie> timeZoneCookie = CookieUtil.getCookies(CommonConstants.TIMEZONE_NAME);
            if (!timeZoneCookie.isEmpty()) {
                TimeZone timeZone = TimeZone.getTimeZone(timeZoneCookie.get(0).getValue());
                LocaleContextHolder.setTimeZone(timeZone);
            }
        }

        return true;
    }
}
