package com.sptek._frameworkWebCore.filter;

import com.sptek._frameworkWebCore.annotation.EnableNoFilterAndSessionForMinorRequest_InMain;
import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import com.sptek._frameworkWebCore.base.constant.MainClassAnnotationRegister;
import com.sptek._frameworkWebCore.commonObject.vo.CorsPropertiesVo;
import com.sptek._frameworkWebCore.util.SecurityUtil;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
//@Profile(value = { "local", "dev", "stg", "prd" })
//@HasAnnotationOnMain_InBean(EnableCorsPolicyFilter_InMain.class)
//@WebFilter(urlPatterns = "/api/*") //브라우저에서 실제 CORS 확인 처리는 api 호출때 주로 요첨 됨으로..
public class CorsPolicyFilter extends OncePerRequestFilter {
    // CORS 설정은 SpringSecurity 에서 설정 가능 하나..
    // 상세한 컨트롤 및 어노테이션을 통한 사용 설정을 위해 개별 필터로 처리함(SpringSecurity 의 CORS 디폴트 처리는 disabled 처리함)

    private final CorsPropertiesVo corsPropertiesVo;
    private Boolean enableNoFilterAndSessionForMinorRequest_InMain = null;

    @PostConstruct //Bean 생성 이후 호출
    public void init() {
        log.info(CommonConstants.SERVER_INITIALIZATION_MARK + this.getClass().getSimpleName() + " is Applied.");
    }

    @Override
    public void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        //log.debug("CorsPolicyFilter start");
        // 매번 호출되는 것을 방지 하기 위해서
        if (enableNoFilterAndSessionForMinorRequest_InMain == null) {
            enableNoFilterAndSessionForMinorRequest_InMain = MainClassAnnotationRegister.hasAnnotation(EnableNoFilterAndSessionForMinorRequest_InMain.class);
        }

        // todo: 필터 제외 케이스 를 적용하는게 맞을까? 보안 협의가 필요
        if (enableNoFilterAndSessionForMinorRequest_InMain) {
            if (SecurityUtil.isNotEssentialRequest() || SecurityUtil.isStaticResourceRequest()) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        String origin = Collections.list(request.getHeaderNames()).stream()
                .filter(headerName -> headerName.equalsIgnoreCase("Origin"))
                .findFirst() // Origin 헤더의 첫번째 값을 사용함, 기본적으로 Origin은 하나만 있어야 함
                .map(request::getHeader)
                .orElse("NoOrigin");

        //log.debug("CORS request Orign: {}", origin);

        // 브라우저는 요청 형태에 따라 다양한 CORS 정책을 사용함
        // ex: GET 일때는 Option 요청을 보내지 않고 본래 요청에 Origin 만 넣어서 보냄, POST 등 중요한? 요청의 경우 Option 을 먼저 보내고 안전 한지 확인 후 본래 요청 에도 Origin 을 넣어 보냄)
        if (!origin.equalsIgnoreCase("NoOrigin")) {
            String allowOrigin = "*".equals(corsPropertiesVo.getDefaultAccessControlAllowOrigin()) || corsPropertiesVo.getAccessControlAllowOrigins().contains(origin)
                    ? origin
                    : corsPropertiesVo.getDefaultAccessControlAllowOrigin();

            //log.debug("CORS allowed Origin: {}", allowOrigin);

            response.setHeader("Access-Control-Allow-Origin", allowOrigin);
            response.setHeader("Access-Control-Allow-Methods", corsPropertiesVo.getAccessControlAllowMethods());
            response.setHeader("Access-Control-Allow-Headers", corsPropertiesVo.getAccessControlAllowHeaders());
            response.setHeader("Access-Control-Allow-Credentials", corsPropertiesVo.getAccessControlAllowCredentials());
            response.setHeader("Access-Control-Max-Age", corsPropertiesVo.getAccessControlMaxAge());

            log.debug(origin.equals(allowOrigin) ? "CORS policy validation passed." : "CORS policy validation denied.");
        }

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            //Option 요청 일때는 바로 응답 처리 하고 끝냄
            response.setStatus(HttpServletResponse.SC_OK);

        } else {
            //log.debug("CORS : it's not CORS check request.");
            filterChain.doFilter(request, response); // 다른 요청은 그대로 통과
        }
    }
}


