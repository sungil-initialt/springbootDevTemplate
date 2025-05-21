package com.sptek._frameworkWebCore.filter;

import com.sptek._frameworkWebCore.annotation.EnableNoFilterAndSessionForMinorRequest_InMain;
import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import com.sptek._frameworkWebCore.base.constant.MainClassAnnotationRegister;
import com.sptek._frameworkWebCore.util.SecurityUtil;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;


@Slf4j
//@Profile(value = { "local", "dev", "stg", "prd" })
//@WebFilter(urlPatterns = "/api/*") //ant 표현식 사용 불가 ex: /**
public class MakeRequestTimestampFilter extends OncePerRequestFilter {
    private Boolean enableNoFilterAndSessionForMinorRequest_InMain = null;

    @PostConstruct //Bean 생성 이후 호출
    public void init() {
        log.info(CommonConstants.SERVER_INITIALIZATION_MARK + this.getClass().getSimpleName() + " is Applied.");
    }

    @Override
    public void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        //log.debug("MakeRequestTimestampFilter start");
        // 매번 호출 되는 것을 방지 하기 위해서
        if (enableNoFilterAndSessionForMinorRequest_InMain == null) {
            enableNoFilterAndSessionForMinorRequest_InMain = MainClassAnnotationRegister.hasAnnotation(EnableNoFilterAndSessionForMinorRequest_InMain.class);
        }

        if (enableNoFilterAndSessionForMinorRequest_InMain) {
            if (SecurityUtil.isNotEssentialRequest() || SecurityUtil.isStaticResourceRequest()) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        request.setAttribute(CommonConstants.REQ_PROPERTY_FOR_LOGGING_TIMESTAMP, LocalDateTime.now());
        filterChain.doFilter(request, response);
    }
}
