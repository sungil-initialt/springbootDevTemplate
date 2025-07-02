package com.sptek._frameworkWebCore.controllerAdvice.view;

import com.sptek._frameworkWebCore.annotation.EnableUserAuthenticationToModelAttribute_InMain;
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
@HasAnnotationOnMain_InBean(EnableUserAuthenticationToModelAttribute_InMain.class)
@ControllerAdvice
//@ControllerAdvice(basePackages = {"com.sptek.a", "com.aptek.b"})
//@ControllerAdvice(assignableTypes = {a.class, b.class})
@RequiredArgsConstructor
public class ControllerAdviceForUserAuthenticationToModelAttribute {
    @ModelAttribute
    public void addModelAttributes(Model model) {
        // todo: 실 서비스 에서는 실제 필요한 항목만 선별 하는 것을 고민할 것 (id, 이름, 권한 등)
        model.addAttribute("userAuthentication", SecurityUtil.getMyAuthentication() == null ? "" : SecurityUtil.getMyAuthentication().getPrincipal());
    }
}
