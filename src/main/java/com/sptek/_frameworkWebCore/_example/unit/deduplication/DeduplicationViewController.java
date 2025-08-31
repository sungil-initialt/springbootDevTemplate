package com.sptek._frameworkWebCore._example.unit.deduplication;

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

public class DeduplicationViewController {

    @NonFinal
    private final String htmlBasePath = "pages/_example/unit/";

    // API 테스트를 위한 단순 테스트 페이지 호출용
    @GetMapping("/deduplication/preventDuplicateRequest")
    public String preventDuplicateRequest() {
        return htmlBasePath + "preventDuplicationRequest";
    }
}