package com.sptek._frameworkWebCore.controllerAdvice.view;

import com.sptek._frameworkWebCore.annotation.EnableGlobalViewModelForUserAuthentication_InMain;
import com.sptek._frameworkWebCore.annotation.annotationCondition.HasAnnotationOnMain_InBean;
import com.sptek._frameworkWebCore.util.SecurityUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@Slf4j
@Data
@HasAnnotationOnMain_InBean(EnableGlobalViewModelForUserAuthentication_InMain.class)
@ControllerAdvice
//@ControllerAdvice(basePackages = {"com.sptek.a", "com.aptek.b"})
//@ControllerAdvice(assignableTypes = {a.class, b.class})
@RequiredArgsConstructor
public class GlobalUserAuthenticationViewModelControllerAdvice {
    @ModelAttribute
    public void addModelAttributes(Model model) {
        // todo: 실 서비스에서는 view 에서 실제 필요한 항목으로 선별해서 내릴것.
        model.addAttribute("userAuthentication", SecurityUtil.getUserAuthentication().getPrincipal());
    }
}
