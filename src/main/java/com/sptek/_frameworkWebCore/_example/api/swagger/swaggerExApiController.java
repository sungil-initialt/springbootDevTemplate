package com.sptek._frameworkWebCore._example.api.swagger;

import com.sptek._frameworkWebCore._example.dto.ValidatedDto;
import com.sptek._frameworkWebCore.annotation.EnableResponseOfApiCommonSuccess_InRestController;
import com.sptek._frameworkWebCore.annotation.EnableResponseOfApiGlobalException_InRestController;
import com.sptek._frameworkWebCore.globalVo.ProjectInfoVo;
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
@RequestMapping(value = {"/api/v1/", /*"/api/v2/"*/}, produces = {MediaType.APPLICATION_JSON_VALUE/*, MediaType.APPLICATION_XML_VALUE*/}) // 클라이언트가 Accept 해더를 보낼 경우 제공하는 미디어 타입이 일치해야함(없으면 406)

@Tag(name = "스웨거 예시", description = "스웨거 어노테이션 사용을 위한 예시 api")
public class swaggerExApiController {
    private final ProjectInfoVo projectInfoVo;

    @GetMapping("/echoMessage")
    @Operation(summary = "echoMessage", description = "message echo 테스트", tags = {"echoMessage"})
    @ApiResponse(
            responseCode = "200",
            content = @Content(
                    schema = @Schema(type = "string", description = "응답 메시지", example = "message1:message2")
            )
    )
    public Object echoMessage(
            @Parameter(name = "message1", description = "ehco 로 응답할 내용", required = true) @RequestParam String message1,
            @Parameter(name = "message2", description = "ehco 로 응답할 내용", required = true) @RequestParam String message2) {

        return message1 + ":" + message2;
    }

    @GetMapping("/echoDto")
    @Operation(summary = "echoDto", description = "객체의 echo 테스트", tags = {"echoDto"})
    @ApiResponse(
            responseCode = "200",
            content = @Content(
                    schema = @Schema(implementation = ValidatedDto.class)
            )
    )
    public Object echoDto(ValidatedDto validatedDto) {
        return validatedDto;
    }
}
