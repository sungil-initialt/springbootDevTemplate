package com.sptek.webfw.config.springSecurity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    // 특별히 custom할께 현재로써는 없음 SavedRequestAwareAuthenticationSuccessHandler 가 그데로 동작됨.

//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        response.sendRedirect("/mypage");
//    }

}
