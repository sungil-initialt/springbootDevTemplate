package com.sptek.webfw.config.interceptor;

import com.sptek.webfw.util.ReqResUtil;
import com.sptek.webfw.util.TypeConvertUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.ui.Model;

import java.util.Map;


@Component
@Slf4j
public class ResponseInfoInterceptor implements HandlerInterceptor {

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        String session = request.getSession().getId();
        String url = ReqResUtil.getRequestUrlString(request);
        String header = TypeConvertUtil.strMapToString(ReqResUtil.getRequestHeaderMap(request));
        String datas = TypeConvertUtil.strArrMapToString(ReqResUtil.getRequestParameterMap(request));
        Map<String, Object> modelMap = modelAndView.getModel();

        log.debug("\n----------\nResponse Info Interceptor\nsession : {}\nurlInfo : {}\nheader : {}\ndatas : {}\nmodel: {}\n----------", session, url, header, datas, modelMap);
    }
}

