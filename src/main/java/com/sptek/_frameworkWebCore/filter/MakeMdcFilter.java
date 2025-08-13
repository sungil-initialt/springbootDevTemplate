package com.sptek._frameworkWebCore.filter;

import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import com.sptek._frameworkWebCore.util.AuthenticationUtil;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
//@Profile(value = { "local", "dev", "stg" }) //todo : 상용적용은 고민해 볼것(성능?)
//@HasAnnotationOnMain_InBean(EnableMdcTagging_InMain.class)
//@WebFilter(urlPatterns = "/*")
public class MakeMdcFilter extends OncePerRequestFilter {
    @PostConstruct //Bean 생성 이후 호출
    public void init() {
        log.info(CommonConstants.SERVER_INITIALIZATION_MARK + this.getClass().getSimpleName() + " is Applied.");
    }

    @Override
    public void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        //todo : 성능적 측면에서 오버해드가 발생할 수 있음으로 상용 적용시 고려 필요
        //log.debug("MakeMdcFilter start");
        //Mapped Diagnostic Context 를 사용하여 Slf4j 의 로깅 패턴에 특정 정보를 포함 할수 있도록 한다.

        try {
            // todo: 멤버 계정 사용과 관련한 보안 이슈 체크 필요
            // todo: 로그인 처리 과정 중에 로그를 남기는 경우 아직 CustomUserDetails 객체가 없는 상태일 수 있어 있어서 아래 방식으로 변경함
            // MDC.put("memberId", SecurityUtil.isRealLogin() ? SecurityUtil.getMyCustomUserDetails().getUserDto().getEmail() : "Not Logged In");
            MDC.put("memberId", AuthenticationUtil.isRealLogin() ? AuthenticationUtil.getMyName() : CommonConstants.ANONYMOUS_USER);
            MDC.put("sessionId", request.getSession(true).getId().substring(0, 10));
            filterChain.doFilter(request, response);
        } finally {
            // 요청이 끝난 뒤 반드시 MDC 정리
            MDC.clear();
            //log.debug("MakeMdcFilter clear");
        }
    }
}
