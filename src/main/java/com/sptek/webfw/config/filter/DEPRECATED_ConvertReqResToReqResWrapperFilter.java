package com.sptek.webfw.config.filter;
import com.sptek.webfw.base.constant.CommonConstants;
import com.sptek.webfw.support.HttpServletRequestWrapperSupport;
import com.sptek.webfw.support.HttpServletResponseWrapperSupport;
import com.sptek.webfw.util.SecureUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;


@Slf4j
@Order(1) //httpServletResponseWrapperSupport 형태가 최종 response 형태로 나가야 함으로 필터의 마지막에 처리되야함(마지막 처리를 위해선 가장 먼저 저일되야 함)
@WebFilter(urlPatterns = "/*") //ant 표현식 사용 불가 ex: /**
@ConditionalOnProperty(name = "sptFramework.filters.isEnabled.DEPRECATED_ConvertReqResToReqResWrapperFilter", havingValue = "true", matchIfMissing = false)
public class DEPRECATED_ConvertReqResToReqResWrapperFilter extends OncePerRequestFilter {

    public DEPRECATED_ConvertReqResToReqResWrapperFilter() {
        log.info(CommonConstants.SERVER_INITIALIZATION_MARK + this.getClass().getSimpleName() + " is Applied.");
    }

    @Override
    public void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {

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

    }
}

