package com.sptek._frameworkWebCore._example.unit.deduplication;

import com.sptek._frameworkWebCore._example.dto.ValidatedDto;
import com.sptek._frameworkWebCore.annotation.EnablePreventDuplicateRequest_InRestController_RestControllerMethod;
import com.sptek._frameworkWebCore.annotation.EnableResponseOfViewGlobalException_InViewController;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Slf4j
@RequiredArgsConstructor
@Controller
@EnableResponseOfViewGlobalException_InViewController
@RequestMapping(value = "/view/example/", produces = MediaType.TEXT_HTML_VALUE)

public class DeduplicationViewController {

    @NonFinal
    private final String htmlBasePath = "pages/_example/unit/";

    @GetMapping("/deduplication/preventDuplicateRequest")
    public String preventDuplicateRequest() {
        return htmlBasePath + "preventDuplicationRequest";
    }
}