package com.sptek._frameworkWebCore.interceptor;

import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import com.sptek._frameworkWebCore.util.CookieUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.time.Duration;
import java.util.List;
import java.util.TimeZone;

public class CustomLocaleChangeInterceptor extends LocaleChangeInterceptor {

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws ServletException {
        // locale 처리
        super.preHandle(request, response, handler);

        // locale 처리시 LocaleContextHolder의 timezone 처리도 함께 처리하려고 custom 클레스로 만듬.
        // DateTimeContextHolder 가 따로 있지만.. 대부분의 영역에서 DateTimeContextHolder 가 없으면 LocaleContextHolder의 timezone 을 사용함
        String timeZoneParam = request.getParameter(CommonConstants.TIMEZONE_NAME);
        if (timeZoneParam != null) {
            TimeZone timeZone = TimeZone.getTimeZone(timeZoneParam);
            LocaleContextHolder.setTimeZone(timeZone);
            CookieUtil.createCookieAndAdd(CommonConstants.TIMEZONE_NAME, timeZoneParam, Duration.ofDays(CommonConstants.LOCALE_COOKIE_MAX_AGE_DAY), true, true);

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
