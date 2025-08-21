package com.sptek._frameworkWebCore._example.unit.async;

import com.sptek._frameworkWebCore._annotation.Enable_ResponseOfViewGlobalException_At_ViewController;
import com.sptek._frameworkWebCore.springSecurity.extras.dto.SignupRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequiredArgsConstructor
@Controller
@Enable_ResponseOfViewGlobalException_At_ViewController
@RequestMapping(value = "/view/example/", produces = MediaType.TEXT_HTML_VALUE)

public class AsyncViewController {

    @NonFinal
    private final String htmlBasePath = "pages/_example/unit/";
    private final AsyncService asyncService;


    @GetMapping("/async/xx")
    public String xx(Model model , SignupRequestDto signupRequestDto) {
        return htmlBasePath + "xx";
    }

}
