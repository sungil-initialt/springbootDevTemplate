package com.sptek._frameworkWebCore._example.api.responseError;

import com.sptek._frameworkWebCore._example.dto.ValidatedDto;
import com.sptek._frameworkWebCore.annotation.EnableResponseOfApiCommonSuccess_InRestController;
import com.sptek._frameworkWebCore.annotation.EnableResponseOfApiGlobalException_InRestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@EnableResponseOfApiCommonSuccess_InRestController
@EnableResponseOfApiGlobalException_InRestController
@RequestMapping(value = {"/api/v1/example/responseErrorApi/"}, produces = {MediaType.APPLICATION_JSON_VALUE/*, MediaType.APPLICATION_XML_VALUE*/}) // 클라이언트가 Accept 해더를 보낼 경우 제공하는 미디어 타입이 일치해야함(없으면 406)
@Tag(name = "responseErrorApi", description = "")

public class ResponseErrorApiController {


    @PostMapping("/dtoValidation")
    @Operation(summary = "객체의 validation 오류", description = "")
    public Object echoDto(@RequestBody @Validated ValidatedDto validatedDto) {
        return validatedDto;
    }

    @GetMapping("/missingRequestHeaderException")
    public Object checkHeader(@RequestHeader("X-Auth") String authHeader) {
        return authHeader;
    }
}
