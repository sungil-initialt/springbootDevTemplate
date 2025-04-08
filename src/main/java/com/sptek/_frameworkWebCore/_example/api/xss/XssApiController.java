package com.sptek._frameworkWebCore._example.api.xss;

import com.sptek._frameworkWebCore.annotation.EnableResponseOfApiCommonSuccess_InRestController;
import com.sptek._frameworkWebCore.annotation.EnableResponseOfApiGlobalException_InRestController;
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
@EnableResponseOfApiCommonSuccess_InRestController
@EnableResponseOfApiGlobalException_InRestController
@RequestMapping(value = {"/api/v1/example/xss/"}, produces = {MediaType.APPLICATION_JSON_VALUE/*, MediaType.APPLICATION_XML_VALUE*/}) // 클라이언트가 Accept 해더를 보낼 경우 제공하는 미디어 타입이 일치해야함(없으면 406)
@Tag(name = "xss", description = "")

public class XssApiController {

    @GetMapping("/XssProtect")
    @Operation(summary = "parameter의 스크립트 요소를 HTML entity encoding 처리하여 controller로 자동 바인딩", description = "")
    public Object XssProtectGet(@Parameter(name = "originParameter", description = "스크립트 요소를 포함한 요청 파람") @RequestParam String originParameter) {
        return "XssProtectedText = " + originParameter;
    }

    @PostMapping("/XssProtect")
    @Operation(summary = "body의 스크립트 요소를 HTML entity encoding 처리하여 controller로 자동 바인딩", description = "")
    //post Req에 대한 xss 처리 결과 확인
    public Object XssProtectPost(@Parameter(name = "originBody", description = "스크립트 요소를 포함한 요청 body") @RequestBody String originBody) {
        return "XssProtectedText = " + originBody;
    }
}
