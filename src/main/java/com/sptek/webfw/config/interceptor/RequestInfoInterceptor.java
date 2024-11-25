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
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String session = request.getSession().getId();
        String methodType = ReqResUtil.getRequestMethodType(request);
        String url = ReqResUtil.getRequestUrlString(request);
        String header = TypeConvertUtil.strMapToString(ReqResUtil.getRequestHeaderMap(request));
        String datas = TypeConvertUtil.strArrMapToString(ReqResUtil.getRequestParameterMap(request));

        log.debug("\n----------\nRequest Info Interceptor\nsession : {}\n({}) url : {}\nheader : {}\ndatas : {}\n----------", session, methodType, url, header, datas);
        return true;
    }
}

