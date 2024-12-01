package com.sptek.webfw.config.springSecurity;

import com.sptek.webfw.config.springSecurity.spt.LoginHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
// 현재는 SavedRequestAwareAuthenticationSuccessHandler 의 옵션 설정 외 그데로 사용.

    private final static String LOGIN_SUCCESS_TARGETURL_PARAMETER = "redirectTo";

    //필요 옵션 설정
    CustomLoginSuccessHandler() {
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
