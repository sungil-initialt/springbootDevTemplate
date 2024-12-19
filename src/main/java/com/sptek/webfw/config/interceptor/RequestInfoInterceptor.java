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
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class RequestInfoInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(request instanceof ContentCachingRequestWrapper)) {
            request = new ContentCachingRequestWrapper(request);
        }
        if (!(response instanceof ContentCachingResponseWrapper)) {
            response = new ContentCachingResponseWrapper(response);
        }
        return true;
    }

    //-->여기 수정해야함
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        if (request instanceof ContentCachingRequestWrapper && response instanceof ContentCachingResponseWrapper) {
            String session = request.getSession().getId();
            String methodType = ReqResUtil.getRequestMethodType(request);
            String url = ReqResUtil.getRequestUrlString(request);
            String header = TypeConvertUtil.strMapToString(ReqResUtil.getRequestHeaderMap(request));
            String params = TypeConvertUtil.strArrMapToString(ReqResUtil.getRequestParameterMap(request));
            Map<String, Object> modelMap = Optional.ofNullable(modelAndView).map(ModelAndView::getModel).orElse(Collections.emptyMap());

            // Read request body
            ContentCachingRequestWrapper cachingRequest = (ContentCachingRequestWrapper) request;
            String requestBody = new String(cachingRequest.getContentAsByteArray(), request.getCharacterEncoding());

            ContentCachingResponseWrapper cachingResponse = (ContentCachingResponseWrapper) response;
            String responseBody = new String(cachingResponse.getContentAsByteArray(), request.getCharacterEncoding());

            log.debug("\n----------\n--> Request Info Interceptor\nsession : {}\n({}) url : {}\nheader : {}\nparams : {} \nmodelMap : {}\nrequestBody : {}\nresponseBody : {}\n----------\n", session, methodType, url, header, params, modelMap, requestBody, responseBody);
            // Important: Copy the response body to the original response
            ((ContentCachingResponseWrapper) response).copyBodyToResponse();
        }

        String session = request.getSession().getId();
        String methodType = ReqResUtil.getRequestMethodType(request);
        String url = ReqResUtil.getRequestUrlString(request);
        String header = TypeConvertUtil.strMapToString(ReqResUtil.getRequestHeaderMap(request));
        String params = TypeConvertUtil.strArrMapToString(ReqResUtil.getRequestParameterMap(request));

        // Read request body
        ContentCachingRequestWrapper cachingRequest = (ContentCachingRequestWrapper) request;
        String requestBody = new String(cachingRequest.getContentAsByteArray(), request.getCharacterEncoding());

        ContentCachingRequestWrapper cachingRequest = (ContentCachingRequestWrapper) request;
        String requestBody = new String(cachingRequest.getContentAsByteArray(), request.getCharacterEncoding());

        log.debug("\n----------\n--> Request Info Interceptor\nsession : {}\n({}) url : {}\nheader : {}\nparams : {} \nrequestBody : {}\n----------\n", session, methodType, url, header, params, requestBody);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        // Nothing to do here for now
    }
}

