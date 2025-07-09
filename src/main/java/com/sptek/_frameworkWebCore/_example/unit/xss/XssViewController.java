package com.sptek._frameworkWebCore._example.unit.xss;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sptek._frameworkWebCore.annotation.EnableResponseOfViewGlobalException_InViewController;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Controller
@EnableResponseOfViewGlobalException_InViewController
@RequestMapping(value = "/view/example/", produces = MediaType.TEXT_HTML_VALUE)

public class XssViewController {

    @NonFinal
    private final String htmlBasePath = "pages/_example/unit/";

    //thymeleaf 에서 폼 입력 데이터 에 대한 validation 에러 처리 방법
    @GetMapping("/xss/xssProtect")
    public String xssProtectForViewModel(Model model, @RequestParam String originParameter) throws JsonProcessingException {
        Map<String, String> testResult = new HashMap<>();
        testResult.put("originData", originParameter);
        
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.getFactory().setCharacterEscapes(new XssProtectHelper());
//        String xssProtectedData = mapper.writeValueAsString(originParameter);
//        testResult.put("xssProtectedData", xssProtectedData);
        
        model.addAttribute("result", testResult);
        return htmlBasePath + "simpleModelView";
    }

}
