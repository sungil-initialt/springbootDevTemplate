package com.sptek._frameworkWebCore._example.unit.xss;

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
@RequestMapping(value = {"/api/"}, produces = {MediaType.APPLICATION_JSON_VALUE/*, MediaType.APPLICATION_XML_VALUE*/})
@Tag(name = "xss", description = "")

public class XssApiController {

    @GetMapping("/01/example/xss/xssProtect")
    @Operation(summary = "01. parameter의 스크립트 요소를 HTML entity encoding 처리하여 controller로 자동 바인딩", description = "")
    public Object XssProtectGet(@Parameter(name = "originParameter", description = "스크립트 요소를 포함한 요청 파람") @RequestParam String originParameter) {
        return originParameter;
    }

    @PostMapping("/02/example/xss/xssProtect")
    @Operation(summary = "02. body의 스크립트 요소를 HTML entity encoding 처리하여 controller로 자동 바인딩", description = "")
    //post Req에 대한 xss 처리 결과 확인
    public Object XssProtectPost(@Parameter(name = "originBody", description = "스크립트 요소를 포함한 요청 body") @RequestBody String originBody) {
        return originBody;
    }
}
