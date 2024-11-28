package com.sptek.webfw.config.springSecurity;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
// 특별히 custom할께 현재로써는 없어서 현재는 SavedRequestAwareAuthenticationSuccessHandler 의 옵션 설정 외 그데로 사용.

    //필요 옵션 설정
    CustomLoginSuccessHandler() {
        this.setDefaultTargetUrl("/");
        this.setTargetUrlParameter("redirectTo");
    }

//    @Override
//    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
//        // Referer 헤더 가져오기
//        String referer = request.getHeader("referer");
//        log.debug("the page before login (referer) : {}", referer);
//
//        // Referer가 있다면 해당 URL로 리다이렉트
//        if (referer != null && !referer.contains("/login")) {
//            return referer;
//        }
//
//        return super.determineTargetUrl(request, response);
//    }



//    Example
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        response.sendRedirect("/mypage");
//    }

}
