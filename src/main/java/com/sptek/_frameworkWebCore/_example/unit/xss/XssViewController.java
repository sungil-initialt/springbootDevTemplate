package com.sptek._frameworkWebCore._example.unit.xss;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sptek._frameworkWebCore.annotation.Enable_ResponseOfViewGlobalException_At_ViewController;
import com.sptek._frameworkWebCore.annotation.Enable_XssProtectorForView_At_ControllerMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Controller
@Enable_ResponseOfViewGlobalException_At_ViewController
@RequestMapping(value = "/view/example/", produces = MediaType.TEXT_HTML_VALUE)

public class XssViewController {

    @NonFinal
    private final String htmlBasePath = "pages/_example/unit/";

    // Xss 처리 안됨
    @GetMapping("/xss/xssProtectOff")
    public String xssProtectForViewModelOff(Model model) throws JsonProcessingException {
        model.addAttribute("result", makeTestMap());
        return htmlBasePath + "simpleModelView";
    }

    // Xss 자동 처리 됨
    @Enable_XssProtectorForView_At_ControllerMethod
    @GetMapping("/xss/xssProtectOn")
    public String xssProtectForViewModelOn(Model model) throws JsonProcessingException {

        model.addAttribute("result", makeTestMap());
        return htmlBasePath + "simpleModelView";
    }

    private Map<String, Object> makeTestMap() {
        Map<String, Object> testMap = new HashMap<>();
        testMap.put("String", "<script>alert('Oops!')</script>");
        testMap.put("List", List.of("<script>", "<html>"));
        testMap.put("Set", Set.of("<script>", "<html>"));
        testMap.put("Object", new XssTestDto("plain text", "<script></script>", "<html></html>"));
        return testMap;
    }

    @AllArgsConstructor
    @Data
    private class XssTestDto {
        private String field1;
        private String field2;
        private String field3;
    }

}
