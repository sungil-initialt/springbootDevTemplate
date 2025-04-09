package com.sptek._frameworkWebCore._example.api.swagger;

import com.sptek._frameworkWebCore._example.dto.ValidatedDto;
import com.sptek._frameworkWebCore.annotation.EnableResponseOfApiCommonSuccess_InRestController;
import com.sptek._frameworkWebCore.annotation.EnableResponseOfApiGlobalException_InRestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@EnableResponseOfApiCommonSuccess_InRestController
@EnableResponseOfApiGlobalException_InRestController
@RequestMapping(value = {"/api/v1/example/swagger/"}, produces = {MediaType.APPLICATION_JSON_VALUE/*, MediaType.APPLICATION_XML_VALUE*/})
@Tag(name = "swagger", description = "")

public class SwaggerApiController {

    @GetMapping("/echoMessage")
    @Operation(summary = "parameter echo 테스트", description = "", tags = {""})
    @ApiResponse(content = @Content(schema = @Schema(type = "string", description = "응답 메시지", example = "message1:message2")))
    public Object echoMessage(
            @Parameter(description = "echo 내용1") @RequestParam("message1") String message1,
            @Parameter(description = "echo 내용2") @RequestParam(name="message2", required = false) String message2) {

        return message1 + ":" + message2;
    }

    @GetMapping("/echoDto")
    @Operation(summary = "객체의 echo 테스트", description = "", tags = {""})
    @ApiResponse(content = @Content(schema = @Schema(implementation = ValidatedDto.class)))
    public Object echoDto(ValidatedDto validatedDto) {
        return validatedDto;
    }
}
