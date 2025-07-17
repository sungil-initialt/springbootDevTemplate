package com.sptek._frameworkWebCore.interceptor;

import com.sptek._frameworkWebCore.annotation.Enable_UvCheckLog_At_Main;
import com.sptek._frameworkWebCore.annotation.annotationCondition.HasAnnotationOnMain_At_Bean;
import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import com.sptek._frameworkWebCore.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

/*
UV 관련 처리를 위한 인터셉터
 */
@HasAnnotationOnMain_At_Bean(Enable_UvCheckLog_At_Main.class)
@Slf4j
@Component

public class UvCheckLogInterceptor implements HandlerInterceptor{
    private final String uvCheckCookieName = "forUvCheck";
    private final String uvCheckCookieValue = "daily";

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        String uvCheckLog = Optional.ofNullable(CookieUtil.getCookies(uvCheckCookieName))
                .filter(cookies -> !cookies.isEmpty())
                .map(cookies -> "not new visitor")
                .orElse(CommonConstants.UV_CHECK_LOG_NEW_VISITOR);

        if (uvCheckLog.equals(CommonConstants.UV_CHECK_LOG_NEW_VISITOR)) {
            //로깅을 남기는 게 중요 역할 임으로 주석 처리 하지 말것
            log.info(uvCheckLog);

            //오늘 까지 유효한 쿠키로 생성
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime tomorrowStart = now.toLocalDate().plusDays(1).atStartOfDay();
            long remainingSecondsOfToday =  Duration.between(now, tomorrowStart).getSeconds();

            ResponseCookie.ResponseCookieBuilder cookieBuilder = ResponseCookie.from(uvCheckCookieName, uvCheckCookieValue)
                    .maxAge(remainingSecondsOfToday).path("/").sameSite("Strict");
            CookieUtil.addResponseCookie(cookieBuilder.build());
        }

        return true;
    }

}

