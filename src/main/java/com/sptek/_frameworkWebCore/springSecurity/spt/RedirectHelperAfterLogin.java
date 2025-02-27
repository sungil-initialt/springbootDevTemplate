package com.sptek._frameworkWebCore.springSecurity.spt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@Slf4j
public class RedirectHelperAfterLogin {

    private final static String LOGIN_SUCCESS_REDIRECT_URL = "LOGIN_SUCCESS_REDIRECT_URL";
    private final static String THE_TIME_SPRING_OWN_REDIRECT_URL = "THE_TIME_SPRING_OWN_REDIRECT_URL";

    private final static HttpSessionRequestCache requestCache = new HttpSessionRequestCache();

    public static String getRedirectUrlAfterLogging(HttpServletRequest request, HttpServletResponse response) {
        String referer = request.getHeader("referer");
        String refererPath = "";
        String refererQuery = "";
        String redirectParam = "";

        if(referer != null) {
            try {
                URI uri = new URI(referer);
                refererPath = uri.getPath() == null ? "" : uri.getPath(); // 경로 (/ 이후 부분)
                refererQuery = uri.getQuery() == null ? "" : uri.getQuery(); // 쿼리 문자열 (? 이후 부분)

                log.debug("referer uri({}), refererPath({}), refererQuery({})", referer, refererPath, refererQuery);
                redirectParam = refererQuery.isEmpty() ? refererPath : refererPath + "?" + refererQuery;

            } catch (URISyntaxException e) {
                log.debug(e.getMessage());
            }
        }

        //url 로 직접 치고 들어온 케이스 (referer 가 없는 경우)
        if(refererPath.equals("")) {
            removeRedirectUrlSpringOwn(request, response);
            request.getSession().removeAttribute(LOGIN_SUCCESS_REDIRECT_URL);
            log.debug("came into the login by typing the login url.");
        }

        //로그인 버튼을 클릭해서 들어온 케이스
        if(request.getParameter("button") != null) {
            removeRedirectUrlSpringOwn(request, response);
            log.debug("came into the login by click the login button.");
        }

        //referer 를 로그인 성공시 redirect url 설정 (단 로그인 페이지에서 로그인 실패 또는 logout 등 이유로.. referer 가 login 자신이 되는 케이스는 제외하고)
        if (!refererPath.equals("/login")) {
            request.getSession().setAttribute(LOGIN_SUCCESS_REDIRECT_URL, redirectParam);
        }

        String attrLoginSuccessRedirectUrl = Optional.ofNullable(request.getSession().getAttribute(LOGIN_SUCCESS_REDIRECT_URL)).map(Object::toString).orElse("");
        log.debug("helper's redirect url : {}", attrLoginSuccessRedirectUrl);
        log.debug("spring's redirect url : {}", getRedirectUrlSpringOwn(request, response));

        //위 케이스별 처리에도 불고하고 SpringOwn RedirectUrl 이 존재한다면 spring 에게 Redirect 처리를 맞기기 위해 redirectParam 을 null 로 내림
        return hasOkRedirectUrlSpringOwn(request, response) ? null :  attrLoginSuccessRedirectUrl;
    }

    public static boolean hasOkRedirectUrlSpringOwn(HttpServletRequest request, HttpServletResponse response) {
        //Spring-security는 post 요청에 대해서는 인증 후 saveRequest를 생성하지 않는다.. 보안상 민감할수도 있는 데이터를 session 등에 저장 하지 않기 위해.
        //이걸 custom 클레스에서 overwirte 해서 강제로 savedRequest를 만들수도 있지만... CSRF토큰을 사용하는 경우 최초 form 의 csrf 토큰값이 로그인후 변경되기 때문에 또 문제가 있다.
        //억지로 구현하려하지 말고 가능하면... 이런 한계를 그데로 그데로 받아드리는게 좋을듯 함

        SavedRequest savedRequest =  requestCache.getRequest(request, response);
        String savedRequestRedirectPath = "";

         if(savedRequest != null) {
           String url = savedRequest.getRedirectUrl();
             try {
                 URI uri = new URI(url);
                 savedRequestRedirectPath = uri.getPath() == null ? "" : uri.getPath(); // 경로 (/ 이후 부분)
             } catch (URISyntaxException e) {
                 log.debug(e.getMessage());
             }
         }

         return savedRequest != null && (!savedRequestRedirectPath.equals("/login") && !savedRequestRedirectPath.equals("login"));
    }

    public static String getRedirectUrlSpringOwn(HttpServletRequest request, HttpServletResponse response) {
        return requestCache.getRequest(request, response) == null ? null : requestCache.getRequest(request, response).getRedirectUrl();
    }

    public static void removeRedirectUrlSpringOwn(HttpServletRequest request, HttpServletResponse response) {
        //request.getSession().removeAttribute("SPRING_SECURITY_SAVED_REQUEST"); // todo : 어떤게 더 좋은 방법일까?
        requestCache.removeRequest(request, response);
    }
}