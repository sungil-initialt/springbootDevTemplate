package com.sptek._frameworkWebCore.springSecurity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component

// 현재는 SavedRequestAwareAuthenticationSuccessHandler 의 옵션 설정 외 그데로 사용.
public class CustomAuthenticationSuccessHandlerForView extends SavedRequestAwareAuthenticationSuccessHandler {
    public final static String LOGIN_SUCCESS_TARGETURL_PARAMETER = "redirectTo";

    //초기 필요 옵션 설정
    CustomAuthenticationSuccessHandlerForView() {
        this.setDefaultTargetUrl("/");
        this.setTargetUrlParameter(LOGIN_SUCCESS_TARGETURL_PARAMETER);
    }

//    @Override
//    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
//
//    }

//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
//
//    }

}
