package com.sptek._frameworkWebCore._example.api.validation;

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
@RequestMapping(value = {"/api/v1/example/"}, produces = {MediaType.APPLICATION_JSON_VALUE/*, MediaType.APPLICATION_XML_VALUE*/}) // 클라이언트가 Accept 해더를 보낼 경우 제공하는 미디어 타입이 일치해야함(없으면 406)
@Tag(name = "Validation annotation 처리 예시", description = "")

public class ValidationExApiController {

    @GetMapping("/validationAnnotation")
    @Operation(summary = "parameter 의 validation 확인과 에러 처리", description = "", tags = {""})
    public Object validationAnnotationGet(@Validated ValidatedDto validationTestDto) {
        return validationTestDto;
    }

    @PostMapping("/validationAnnotation")
    @Operation(summary = "body 의 validation 확인과 에러 처리", description = "", tags = {""})
    public Object validationAnnotation(@RequestBody @Validated ValidatedDto validationTestDto) {
        return validationTestDto;
    }

    @PostMapping("/validationAnnotationIgnore")
    @Operation(summary = "body 의 validation 미적용 처리", description = "", tags = {""})
    public Object validationAnnotationIgnore(@RequestBody ValidatedDto validationTestDto) {
        return validationTestDto;
    }
}
