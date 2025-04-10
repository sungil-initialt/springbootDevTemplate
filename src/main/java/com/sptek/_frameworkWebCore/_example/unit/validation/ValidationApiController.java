package com.sptek._frameworkWebCore._example.unit.validation;

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
@RequestMapping(value = {"/api/v1/example/validation/"}, produces = {MediaType.APPLICATION_JSON_VALUE/*, MediaType.APPLICATION_XML_VALUE*/})
@Tag(name = "validation", description = "")

public class ValidationApiController {

    @GetMapping("/a/validationAnnotation")
    @Operation(summary = "parameter 의 validation 확인과 에러 처리", description = "")
    public Object validationAnnotationGet(@Validated ValidatedDto validationTestDto) {
        return validationTestDto;
    }

    @PostMapping("/b/validationAnnotation")
    @Operation(summary = "body 의 validation 확인과 에러 처리", description = "")
    public Object validationAnnotation(@RequestBody @Validated ValidatedDto validationTestDto) {
        return validationTestDto;
    }

    @PostMapping("/c/validationAnnotationIgnore")
    @Operation(summary = "body 의 validation 미적용 처리", description = "")
    public Object validationAnnotationIgnore(@RequestBody ValidatedDto validationTestDto) {
        return validationTestDto;
    }
}
