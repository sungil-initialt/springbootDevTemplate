package com.sptek._frameworkWebCore.controllerAdvice.view;

import com.sptek._frameworkWebCore.annotation.EnableUserAuthenticationToModelAttribute_InMain;
import com.sptek._frameworkWebCore.annotation.annotationCondition.HasAnnotationOnMain_InBean;
import com.sptek._frameworkWebCore.util.AuthenticationUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@Slf4j
@Data
@HasAnnotationOnMain_InBean(EnableUserAuthenticationToModelAttribute_InMain.class)
@ControllerAdvice
//@ControllerAdvice(basePackages = {"com.sptek.a", "com.aptek.b"})
//@ControllerAdvice(assignableTypes = {a.class, b.class})
@RequiredArgsConstructor
public class ControllerAdviceForUserAuthenticationToModelAttribute {

    @ModelAttribute
    public void addModelAttributes(Model model) throws Exception {
        model.addAttribute("isLogin", AuthenticationUtil.isRealLogin());

        if (AuthenticationUtil.isRealLogin()) {
            //model.addAttribute("UserDto", ((CustomUserDetails)SecurityUtil.getMyAuthentication().getPrincipal()).getUserDto());
            model.addAttribute("userId", AuthenticationUtil.getMyId());
            model.addAttribute("userName", AuthenticationUtil.getMyName());
            model.addAttribute("userEmail", AuthenticationUtil.getMyEmail());
            model.addAttribute("userRoles", AuthenticationUtil.getMyRoles());
            model.addAttribute("userAuths", AuthenticationUtil.getMyAuths());
        }
    }
}
