package com.sptek._frameworkWebCore._example.unit.requestFetch;

import com.sptek._frameworkWebCore._annotation.Enable_ResponseOfViewGlobalException_At_ViewController;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequiredArgsConstructor
@Controller
@Enable_ResponseOfViewGlobalException_At_ViewController
@RequestMapping(value = "/view/example/", produces = MediaType.TEXT_HTML_VALUE)

public class RequestFetchViewController {

    @NonFinal
    private final String htmlBasePath = "pages/_example/unit/";

    @GetMapping("/jsApiCall/requestFetch")
    public String preventDuplicateRequest() {
        htmlBasePath = "a";
        return htmlBasePath + "requestFetch";
    }
}