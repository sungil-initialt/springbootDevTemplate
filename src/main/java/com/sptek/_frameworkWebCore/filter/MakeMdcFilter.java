package com.sptek._frameworkWebCore.filter;

import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import com.sptek._frameworkWebCore.util.SecurityUtil;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Slf4j
//@Profile(value = { "local", "dev", "stg" }) //todo : 상용적용은 고민해 볼것(성능?)
//@HasAnnotationOnMain_InBean(EnableMdcTagging_InMain.class)
//@WebFilter(urlPatterns = "/*")
public class MakeMdcFilter extends OncePerRequestFilter {
    //private Boolean enableNoFilterAndSessionForMinorRequest_InMain = null;

    @PostConstruct //Bean 생성 이후 호출
    public void init() {
        log.info(CommonConstants.SERVER_INITIALIZATION_MARK + this.getClass().getSimpleName() + " is Applied.");
    }

    @Override
    public void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        log.debug("MakeMdcFilter start");
        //Mapped Diagnostic Context 를 사용하여 Slf4j 의 로깅 패턴에 특정 정보를 포함 할수 있도록 한다.
        //todo : 성능적 측면에서 오버해드가 발생할 수 있음으로 상용 적용시 고려 필요


// Mdc 필터의 경우 모든 케이스에 적용하는 것으로 변경
//        // 매번 호출 되는 것을 방지 하기 위해서
//        if (enableNoFilterAndSessionForMinorRequest_InMain == null) {
//            enableNoFilterAndSessionForMinorRequest_InMain = SpringUtil.hasAnnotationOnMain(EnableNoFilterAndSessionForMinorRequest_InMain.class);
//        }
//
//        if (enableNoFilterAndSessionForMinorRequest_InMain) {
//            if (SecurityUtil.isNotEssentialRequest() || SecurityUtil.isStaticResourceRequest()) {
//                filterChain.doFilter(request, response);
//                return;
//            }
//        }

        try {
            // todo: 멤버 계정을 보여주는 부분은 해당 시스템 별로 변경이 필요할수 있음, 또한 상용 적용시 보안이슈 체크 필요
            MDC.put("sessionId", request.getSession(true).getId());
            MDC.put("memberId", Optional.ofNullable(SecurityUtil.getUserAuthentication()).map(Authentication::getName).orElse("No Authentication"));
            filterChain.doFilter(request, response);
        } finally {
            // 요청이 끝난 뒤 반드시 MDC 정리
            MDC.clear();
            //log.debug("MakeMdcFilter clear");
        }
    }
}
