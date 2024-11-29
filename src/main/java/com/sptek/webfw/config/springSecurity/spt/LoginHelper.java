package com.sptek.webfw.config.springSecurity.spt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@Slf4j
public class LoginHelper {

    public final static String LOGIN_SUCCESS_TARGETURL_PARAMETER = "redirectTo";
    private final static HttpSessionRequestCache requestCache = new HttpSessionRequestCache();

    public static String getRedirectUrlAfterLogging(HttpServletRequest request, HttpServletResponse response) {
        String referer = request.getHeader("referer");
        String refererPath = "";
        String refererQuery = "";
        String directTo = "";

        if(referer != null) {
            try {
                URI uri = new URI(referer);
                refererPath = uri.getPath() == null ? "" : uri.getPath(); // 경로 (/ 이후 부분)
                refererQuery = uri.getQuery() == null ? "" : uri.getQuery(); // 쿼리 문자열 (? 이후 부분)

                directTo = !refererQuery.isEmpty() ? refererPath + "?" + refererQuery : refererPath;
            } catch (URISyntaxException e) {
                log.debug(e.getMessage());
            }
        }

        //-----------------------------------------------------------------------------------------------------
        // 한가지 처리하지 못한케이스 :
        // 인증이 필요한 url을 브라우저에 직접쳐서.. 로그인 페이지로 자동 이동된 후... 로그인 처리후 처음에 입력한 페이지로 가지 못하고 root로 가는 케이스
        // (다른 사이트도 이런경우는 많은 것 같음.. 처리가 애매함)
        if (refererPath.equals("/login")) {
            if (requestCache.getRequest(request, response) != null) {
                request.getSession().removeAttribute(LOGIN_SUCCESS_TARGETURL_PARAMETER);
            }

        } else {
            request.getSession().setAttribute(LOGIN_SUCCESS_TARGETURL_PARAMETER, directTo);
            requestCache.removeRequest(request, response);
        }

        String result =  (request.getSession().getAttribute(LOGIN_SUCCESS_TARGETURL_PARAMETER) != null)
                ? request.getSession().getAttribute(LOGIN_SUCCESS_TARGETURL_PARAMETER).toString()
                : null;

        Optional<String> resultOpt = Optional.ofNullable(result);
        resultOpt.ifPresentOrElse(
                value -> log.debug("this login will redirect to " + value),
                () -> log.debug("this login will redirect to root(/) or saved request's redirectUrl"));

        return result;
    }
}