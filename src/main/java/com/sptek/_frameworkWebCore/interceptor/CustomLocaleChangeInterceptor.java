package com.sptek._frameworkWebCore.interceptor;

import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import com.sptek._frameworkWebCore.util.CookieUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.format.datetime.standard.DateTimeContext;
import org.springframework.format.datetime.standard.DateTimeContextHolder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.time.Duration;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

public class CustomLocaleChangeInterceptor extends LocaleChangeInterceptor {

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws ServletException {
        if (request.getParameter(getParamName()) != null) {
            super.preHandle(request, response, handler); // locale Param 이 있을 경우 내부에서 setLocale 및 cookie 까지 생성

        } else {
            // cookie 만 있다면 maxAge 연장을 위해 재 생성
            List<Cookie> localeCookies = CookieUtil.getCookies(CommonConstants.LOCALE_COOKIE_NAME);
            if (!localeCookies.isEmpty()) {
                LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
                if (localeResolver != null) {
                    // 내부에서 setLocale 및 cookie 까지 재 생성
                    localeResolver.setLocale(request, response, super.parseLocaleValue(localeCookies.get(0).getValue()));
                }
            }
        }

        // locale 처리시 LocaleContextHolder의 timezone 처리도 함께 처리하려고 custom 클레스로 만듬.
        // DateTimeContextHolder 가 따로 있지만.. 대부분의 영역에서 DateTimeContextHolder 가 없으면 LocaleContextHolder의 timezone 을 사용함
        String timeZoneValue = request.getParameter(CommonConstants.TIMEZONE_COOKIE_NAME);
        List<Cookie> timeZoneCookies = CookieUtil.getCookies(CommonConstants.TIMEZONE_COOKIE_NAME);
        if (timeZoneValue == null && !timeZoneCookies.isEmpty()) {
            timeZoneValue = timeZoneCookies.get(0).getValue();
        }

        if (timeZoneValue != null) {
            TimeZone timeZone = TimeZone.getTimeZone(ZoneId.of(timeZoneValue));
            LocaleContextHolder.setTimeZone(timeZone);
            CookieUtil.createCookieAndAdd(CommonConstants.TIMEZONE_COOKIE_NAME, timeZoneValue, Duration.ofDays(CommonConstants.TIMEZONE_COOKIE_MAX_AGE_DAY));

            // DateTimeContextHolder 까지 추가로 설정
            var ctx = Optional.ofNullable(DateTimeContextHolder.getDateTimeContext()).orElseGet(DateTimeContext::new);
            ctx.setTimeZone(ZoneId.of(timeZoneValue));
            DateTimeContextHolder.setDateTimeContext(ctx);
        }

        return true;
    }
}
