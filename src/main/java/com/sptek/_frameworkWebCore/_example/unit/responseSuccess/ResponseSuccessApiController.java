package com.sptek._frameworkWebCore._example.unit.responseSuccess;

import com.sptek._frameworkWebCore._example.dto.ValidatedDto;
import com.sptek._frameworkWebCore.annotation.EnableResponseOfApiCommonSuccess_InRestController;
import com.sptek._frameworkWebCore.annotation.EnableResponseOfApiGlobalException_InRestController;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping(value = {"/api/v1/example/"}, produces = {MediaType.APPLICATION_JSON_VALUE/*, MediaType.APPLICATION_XML_VALUE*/})
@Tag(name = "response Success", description = "")

public class ResponseSuccessApiController {
    @GetMapping("/public/responseSuccess/a_responseSuccessForPrimitiveType")
    @Operation(summary = "Primitive Type 의 성공 응답", description = "")
    public Object responseSuccessForPrimitiveType(@RequestParam(name="message", required = false) String message) {
        return message;
    }

    @PostMapping("/public/responseSuccess/b_responseSuccessForObject")
    @Operation(summary = "Object(DTO) Type 의 성공 응답", description = "")
    public Object responseSuccessForObject(ValidatedDto validatedDto) {
        return validatedDto;
    }
}
