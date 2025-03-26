package com.sptek._frameworkWebCore.controllerAdvice;

import com.sptek._frameworkWebCore.globalVo.GlobalModelAttributeVo;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@Slf4j
@Data
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributeAdvice {
    private final GlobalModelAttributeVo globalModelAttributeVo;

    @ModelAttribute
    public void addModelAttributes(Model model) {
        globalModelAttributeVo.getAttributes()
                .forEach(model::addAttribute);
    }

    @PostConstruct //Bean 생성 이후 호출
    public void init() {
        //log.debug("GlobalModelAttributes from property : {}", this);
    }
}
