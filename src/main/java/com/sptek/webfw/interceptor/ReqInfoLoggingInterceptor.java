package com.sptek.webfw.interceptor;

import com.sptek.webfw.util.ReqResUtil;
import com.sptek.webfw.util.TypeConvertUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class ReqInfoLoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        String sessionInfo = "session_id=" + session.getId();
        String urlInfo = ReqResUtil.getRequestUrlString(request);
        String headerInfo = TypeConvertUtil.mapToString(ReqResUtil.getRequestHeaderMap(request), " ");
        String parameterInfo = TypeConvertUtil.mapToString2(ReqResUtil.getRequestParameterMap(request), " ");


        // 로그 출력
        log.info("\n" +
                        "##############################   <REQUEST_INFO>   ##############################\n" +
                        "================================  SESSION  =====================================\n" +
                        "{}\n" +
                        "================================  URL  =========================================\n" +
                        "{}\n" +
                        "================================  HEADER  ======================================\n" +
                        "{}" +
                        "================================  PARAMETER  ===================================\n" +
                        "{}" +
                        "##############################   </REQUEST_INFO>   #############################\n",
                sessionInfo, urlInfo, headerInfo, parameterInfo
        );

        return true;
    }
}

