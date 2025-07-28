package com.sptek._frameworkWebCore._example.unit.xss;

import com.sptek._frameworkWebCore.annotation.Enable_ResponseOfApiCommonSuccess_At_RestController;
import com.sptek._frameworkWebCore.annotation.Enable_ResponseOfApiGlobalException_At_RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@Enable_ResponseOfApiCommonSuccess_At_RestController
@Enable_ResponseOfApiGlobalException_At_RestController
@RequestMapping(value = {"/api/"}, produces = {MediaType.APPLICATION_JSON_VALUE/*, MediaType.APPLICATION_XML_VALUE*/})
@Tag(name = "Xss", description = "")

public class XssApiController {

    @GetMapping("/01/example/xss/xssProtect")
    @Operation(summary = "01. parameter의 스크립트 요소를 HTML entity encoding 처리하여 controller로 자동 바인딩", description = "")
    public Object XssProtectGet(@Parameter(name = "originParameter", description = "스크립트 요소를 포함한 요청 파람") @RequestParam String originParameter) {
        // 실제 컨트롤러에 전달되는 값은 원본값 그데로임(json으로 변환되어 나깔때 HTML Entity 코드로 변함됨)
        return originParameter;
    }

    @PostMapping("/02/example/xss/xssProtect")
    @Operation(summary = "02. body의 스크립트 요소를 HTML entity encoding 처리하여 controller로 자동 바인딩", description = "")
    public Object XssProtectPost(@Parameter(name = "originBody", description = "스크립트 요소를 포함한 요청 body") @RequestBody String originBody) {
        // 실제 컨트롤러에 전달되는 값은 원본값 그데로임(json으로 변환되어 나깔때 HTML Entity 코드로 변함됨)
        return originBody;
    }
}
