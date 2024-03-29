package com.sptek.webfw.config.interceptor;

import com.sptek.webfw.util.ReqResUtil;
import com.sptek.webfw.util.TypeConvertUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class RequestInfoInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.debug("[Interceptor >>> ]");
        String sessionInfo = request.getSession().getId();
        String urlInfo = ReqResUtil.getRequestUrlString(request);
        String headerInfo = TypeConvertUtil.strMapToString(ReqResUtil.getRequestHeaderMap(request));
        String parameterInfo = TypeConvertUtil.strArrMapToString(ReqResUtil.getRequestParameterMap(request));

        log.debug("new request info\nsessionInfo : {}\nurlInfo : {}\nheaderInfo : {}\nparameterInfo : {}", sessionInfo, urlInfo, headerInfo, parameterInfo);
        return true;
    }
}

