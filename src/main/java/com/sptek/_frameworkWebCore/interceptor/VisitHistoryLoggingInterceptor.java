package com.sptek._frameworkWebCore.interceptor;

import com.sptek._frameworkWebCore._annotation.Enable_VisitHistoryLog_At_Main;
import com.sptek._frameworkWebCore._annotation.annotationCondition.HasAnnotationOnMain_At_Bean;
import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import com.sptek._frameworkWebCore.base.constant.MainClassAnnotationRegister;
import com.sptek._frameworkWebCore.util.CookieUtil;
import com.sptek._frameworkWebCore.util.LoggingUtil;
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
@HasAnnotationOnMain_At_Bean(Enable_VisitHistoryLog_At_Main.class)
@Slf4j
@Component

public class VisitHistoryLoggingInterceptor implements HandlerInterceptor{

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        String visitHistoryLog = Optional.ofNullable(CookieUtil.getCookies(CommonConstants.VISIT_HISTORY_COOKIE_NAME))
                .filter(cookies -> !cookies.isEmpty())
                .map(cookies -> CommonConstants.VISIT_HISTORY_EXIST_VISITOR_LOG)
                .orElse(CommonConstants.VISIT_HISTORY_NEW_VISITOR_LOG);

        //로그를 남기는게 주 역함임으로 아래 주석 처리 하지 않도록! (console 에는 로그 처리 되지 않음)
        String logTag = String.valueOf(MainClassAnnotationRegister.getAnnotationAttributes(Enable_VisitHistoryLog_At_Main.class).get("value"));
        log.info(LoggingUtil.makeFwLogSimpleForm(visitHistoryLog, logTag));
        
        //오늘 까지 유효한 쿠키로 생성 (자정 까지 남은 sec), 이미 쿠키가 있는 경우에도 새로 생성 (쿠키 유효기간을 변경한 경우 바로 적용되게 하기 위해)
        LocalDateTime now = LocalDateTime.now();
        long remainingSecondsOfToday = Duration.between(now, now.toLocalDate().plusDays(1).atStartOfDay()).getSeconds();

        ResponseCookie.ResponseCookieBuilder cookieBuilder = ResponseCookie.from(CommonConstants.VISIT_HISTORY_COOKIE_NAME, CommonConstants.VISIT_HISTORY_COOKIE_VALE)
                .maxAge(remainingSecondsOfToday).path("/").sameSite("Strict");
        CookieUtil.addResponseCookie(cookieBuilder.build());

        return true;
    }

}

