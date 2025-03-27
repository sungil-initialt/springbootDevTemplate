package com.sptek._frameworkWebCore.controllerAdvice.view;

import com.sptek._frameworkWebCore.annotation.EnableGlobalViewModelForStatic_InMain;
import com.sptek._frameworkWebCore.annotation.annotationCondition.HasAnnotationOnMain_InBean;
import com.sptek._frameworkWebCore.globalVo.GlobalStaticViewModelVo;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@Slf4j
@Data
@HasAnnotationOnMain_InBean(EnableGlobalViewModelForStatic_InMain.class)
@ControllerAdvice
//@ControllerAdvice(basePackages = {"com.sptek.a", "com.aptek.b"})
//@ControllerAdvice(assignableTypes = {a.class, b.class})
@RequiredArgsConstructor
public class GlobalStaticViewModelControllerAdvice {
    private final GlobalStaticViewModelVo globalStaticViewModelVo;

    @ModelAttribute
    public void addModelAttributes(Model model) {
        globalStaticViewModelVo.getAttributes()
                .forEach(model::addAttribute);
    }

    @PostConstruct //Bean 생성 이후 호출
    public void init() {
        //log.debug("StaticModelAttribute from property : {}", this);
    }
}
