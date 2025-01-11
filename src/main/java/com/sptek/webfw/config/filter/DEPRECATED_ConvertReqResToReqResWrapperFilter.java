package com.sptek.webfw.config.filter;

/*
Xss 방지 필터 목적인데.. request param 으로 들어오는 값들에 대한 처리는 필터에서 적용하기가 애매함 (해당 코드는 request body 에만 Xss 필터가 적용됨)
objectMapper 셋팅에서 XssProtectSupport 클레스를 적용하는 방식으로 처리하여 Xss 처리가 중복처리됨(해당 클레스 제거 가능)
컨트롤러 이전단계에서(필터등) request의 stream을 읽어버리면 컨틀롤러에서는 비어있는 request가 넘어가기 때문에 컨트롤러 이전에 request 를 읽은 경우 아래 코드를 참조하도록 남김
*/


import com.sptek.webfw.support.HttpServletRequestWrapperSupport;
import com.sptek.webfw.support.HttpServletResponseWrapperSupport;
import com.sptek.webfw.util.SecureUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
//@Order(1) //httpServletResponseWrapperSupport 형태가 최종 response 형태로 나가야 함으로 필터의 마지막에 처리되야함(마지막 처리를 위해선 가장 먼저 저일되야 함)
//@WebFilter(urlPatterns = "/*") //ant 표현식 사용 불가 ex: /**
public class DEPRECATED_ConvertReqResToReqResWrapperFilter extends OncePerRequestFilter {
    private final boolean IS_FILTER_ON;

    public DEPRECATED_ConvertReqResToReqResWrapperFilter(@Value("${filters.isEnabled.ConvertReqResToReqResWrapperFilter}") Boolean isFilterOn) {
        IS_FILTER_ON = isFilterOn;
    }

    @Override
    public void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        if(IS_FILTER_ON) {
            //필터 제외 케이스
            if (SecureUtil.isNotEssentialRequest() || SecureUtil.isStaticResourceRequest()) {
                filterChain.doFilter(request, response);
                return;
            }

            HttpServletRequestWrapperSupport httpServletRequestWrapperSupport = request instanceof HttpServletRequestWrapperSupport ? (HttpServletRequestWrapperSupport)request : new HttpServletRequestWrapperSupport(request);
            HttpServletResponseWrapperSupport httpServletResponseWrapperSupport = response instanceof HttpServletResponseWrapperSupport ? (HttpServletResponseWrapperSupport)response : new HttpServletResponseWrapperSupport(response);
            filterChain.doFilter(httpServletRequestWrapperSupport, httpServletResponseWrapperSupport);

            // todo: 중요!! 자신이 response를 HttpServletResponseWrapperSupport로 변환한 최초의 필터라면 response에 body를 최종 write 할 책음을 져야 한다.
            //  (httpServletResponseWrapperSupport가 아닌 response 객체에 써야함)
            if (!(response instanceof HttpServletResponseWrapperSupport)) {
                response.getWriter().write(httpServletResponseWrapperSupport.getResponseBody());
            }

        }else{
            filterChain.doFilter(request, response);
        }
    }
}

