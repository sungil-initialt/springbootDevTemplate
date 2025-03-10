package com.sptek._frameworkWebCore.filter;

import com.sptek._frameworkWebCore.annotation.DisableFilterAndSessionForMinorRequest_InMain;
import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import com.sptek._frameworkWebCore.util.SecurityUtil;
import com.sptek._frameworkWebCore.util.SpringUtil;
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
public class MakeMdcFilter extends OncePerRequestFilter {
    private Boolean hasDisableFilterAndSessionForMinorRequestAnnotation = null;

    @PostConstruct //Bean 생성 이후 호출
    public void init() {
        log.info(CommonConstants.SERVER_INITIALIZATION_MARK + this.getClass().getSimpleName() + " is Applied.");
    }

    @Override
    public void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        //Mapped Diagnostic Context 를 사용하여 Slf4j 의 로깅 패턴에 특정 정보를 포함 할수 있도록 한다.
        //todo : 성능적 측면에서 오버해드가 발생할 수 있음으로 상용 적용시 고려 필요

        // 매번 호출 되는 것을 방지 하기 위해서
        if (hasDisableFilterAndSessionForMinorRequestAnnotation == null) {
            hasDisableFilterAndSessionForMinorRequestAnnotation = SpringUtil.hasAnnotationOnMain(DisableFilterAndSessionForMinorRequest_InMain.class);
        }

        if (hasDisableFilterAndSessionForMinorRequestAnnotation) {
            if (SecurityUtil.isNotEssentialRequest() || SecurityUtil.isStaticResourceRequest()) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        try {
            // 세션 ID를 가져와 MDC에 추가
            MDC.put("sessionId", request.getSession(true).getId());
            // todo: 멤버 계정을 보여주는 부분은 해당 시스템 별로 변경이 필요할수 있음, 또한 상용 적용시 보안이슈 체크 필요

            MDC.put("memberId", Optional.ofNullable(SecurityUtil.getUserAuthentication()).map(Authentication::getName).orElse("ignored security-chains"));
            filterChain.doFilter(request, response);
        } finally {
            // 요청이 끝난 뒤 반드시 MDC 정리
            MDC.clear();
        }
    }
}
