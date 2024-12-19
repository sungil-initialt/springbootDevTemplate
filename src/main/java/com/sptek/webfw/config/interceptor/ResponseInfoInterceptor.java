package com.sptek.webfw.config.interceptor;

import com.sptek.webfw.util.ReqResUtil;
import com.sptek.webfw.util.TypeConvertUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.ContentCachingResponseWrapper;


@Component
@Slf4j
public class ResponseInfoInterceptor implements HandlerInterceptor {
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        if (!(response instanceof ContentCachingResponseWrapper)) {
            response = new ContentCachingResponseWrapper(response);
        }

        String session = request.getSession().getId();
        String url = ReqResUtil.getRequestUrlString(request);
        String header = TypeConvertUtil.strMapToString(ReqResUtil.getRequestHeaderMap(request));
        String params = TypeConvertUtil.strArrMapToString(ReqResUtil.getRequestParameterMap(request));
        String responseBody = new String(((ContentCachingResponseWrapper) response).getContentAsByteArray(), request.getCharacterEncoding());
        ((ContentCachingResponseWrapper) response).copyBodyToResponse();

        log.debug("\n----------\n<-- Response Info Interceptor\nsession : {}\nurlInfo : {}\nheader : {}\nparams : {}\nresponseBody : {}\n----------\n", session, url, header, params, responseBody);

    }
}

