package com.sptek.webfw.config.springSecurity.spt;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
public class LoginHelper {

    private final static HttpSessionRequestCache requestCache = new HttpSessionRequestCache();

    public static String getRedirectQuery(HttpServletRequest request) {
        // SavedRequest 객체가 존재하면 Spring Security에 의해 로그인 페이지로 리다이렉트된 경우 (반대의 경우는 직접 로그인 페이지로 이동한 경우)
        // todo : 이 기준이 적절할까?

        String referer = request.getHeader("referer");
        String path = "";
        String query = "";

        if(referer != null) {
            try {
                URI uri = new URI(referer); // URI 객체 생성
                path = uri.getPath(); // 경로 (/ 이후 부분)
                query = uri.getQuery(); // 쿼리 문자열 (? 이후 부분)

            } catch (URISyntaxException e) {
                log.debug(e.getMessage());
            }
        }

        if(path != null && !path.equals("/login")) {
            request.getSession().setAttribute("redirectTo", referer);
        }

        return  (request.getSession().getAttribute("redirectTo") != null)
                ? "redirectTo=" + request.getSession().getAttribute("redirectTo").toString()
                : "";
    }
    //-->여기 테스트
}