package com.sptek.webfw.config.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sptek.webfw.base.constant.CommonConstants;
import com.sptek.webfw.support.DPRECATED_HttpServletRequestWrapperSupport;
import com.sptek.webfw.util.SecureUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.annotation.WebFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Order(1)
@WebFilter(urlPatterns = "/*") //ant 표현식 사용 불가 ex: /**
@ConditionalOnProperty(name = "sptFramework.filters.isEnabled.DEPRECATED_XssProtectFilter", havingValue = "true", matchIfMissing = false)
public class DEPRECATED_XssProtectFilter extends OncePerRequestFilter {
    /*
    Xss 방지 필터 목적인데.. request param 으로 들어오는 값들에 대한 처리는 필터에서 적용하기가 애매함 (해당 코드는 request body 에만 Xss 필터가 적용됨)
    objectMapper 셋팅에서 XssProtectSupport 클레스를 적용하는 방식으로 처리하여 Xss 처리가 중복처리됨(해당 클레스 제거 가능)
    컨트롤러 이전단계에서(필터등) request의 stream을 읽어버리면 컨틀롤러에서는 비어있는 request가 넘어가기 때문에 컨트롤러 이전에 request 를 읽은 경우 아래 코드를 참조하도록 남김
    */

    public DEPRECATED_XssProtectFilter() {
        log.info(CommonConstants.SERVER_INITIALIZATION_MARK + this.getClass().getSimpleName() + " is Applied.");
    }

    @Override
    public void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {

        //필터 제외 케이스
        if (SecureUtil.isNotEssentialRequest() || SecureUtil.isStaticResourceRequest()) {
            filterChain.doFilter(request, response);
            return;
        }

        DPRECATED_HttpServletRequestWrapperSupport DPRECATEDHttpServletRequestWrapperSupport = new DPRECATED_HttpServletRequestWrapperSupport(request);
        String requestBody = IOUtils.toString(DPRECATEDHttpServletRequestWrapperSupport.getReader());

        if (StringUtils.hasText(requestBody)) {
            Map<String, Object> orgJsonObject = new ObjectMapper().readValue(requestBody, HashMap.class);
            Map<String, Object> newJsonObject = new HashMap<>();
            orgJsonObject.forEach((key, value) -> newJsonObject.put(key, SecureUtil.charEscape(value.toString())));

            //대체 request를 생성해서 넘김
            DPRECATEDHttpServletRequestWrapperSupport.resetInputStream(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(newJsonObject).getBytes());
        }

        filterChain.doFilter(DPRECATEDHttpServletRequestWrapperSupport, response);

    }
}

