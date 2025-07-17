package com.sptek._frameworkWebCore.controllerAdvice.view;

import com.sptek._frameworkWebCore.annotation.Enable_PropertiesToModelAttribute_At_Main;
import com.sptek._frameworkWebCore.annotation.annotationCondition.HasAnnotationOnMain_At_Bean;
import com.sptek._frameworkWebCore.commonObject.vo.PropertiesForModelAttributeVo;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@Slf4j
@Data
@HasAnnotationOnMain_At_Bean(Enable_PropertiesToModelAttribute_At_Main.class)
@ControllerAdvice
//@ControllerAdvice(basePackages = {"com.sptek.a", "com.aptek.b"})
//@ControllerAdvice(assignableTypes = {a.class, b.class})
@RequiredArgsConstructor
public class ControllerAdviceForPropertiesToModelAttribute {
    private final PropertiesForModelAttributeVo propertiesForModelAttributeVo;

    @ModelAttribute
    public void addModelAttributes(Model model) {
        propertiesForModelAttributeVo.getAttributes()
                .forEach(model::addAttribute);
    }

//    @PostConstruct //Bean 생성 이후 호출
//    public void init() {
//        log.debug("StaticModelAttribute from property : {}", this);
//    }
}
