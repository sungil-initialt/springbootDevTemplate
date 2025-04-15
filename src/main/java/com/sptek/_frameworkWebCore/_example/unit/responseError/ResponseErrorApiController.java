package com.sptek._frameworkWebCore._example.unit.responseError;

import com.sptek._frameworkWebCore._example.dto.ValidatedDto;
import com.sptek._frameworkWebCore.annotation.EnableResponseOfApiCommonSuccess_InRestController;
import com.sptek._frameworkWebCore.annotation.EnableResponseOfApiGlobalException_InRestController;
import com.sptek._frameworkWebCore.base.exception.ServiceException;
import com.sptek._projectCommon.code.ServiceErrorCodeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@EnableResponseOfApiCommonSuccess_InRestController
@EnableResponseOfApiGlobalException_InRestController
@RequestMapping(value = {"/api/v1/example/"}, produces = {MediaType.APPLICATION_JSON_VALUE/*, MediaType.APPLICATION_XML_VALUE*/})
@Tag(name = "response Error", description = "")

public class ResponseErrorApiController {
    private final ResponseErrorService responseErrorService;

    @PostMapping("/public/responseError/a_dtoValidation")
    @Operation(summary = "객체의 validation 에러", description = "")
    public Object dtoValidation(@RequestBody ValidatedDto validatedDto) {
        int userIdSize = validatedDto.getUserId().length(); //NPE 발생 시키기 위해 임의 작성
        return validatedDto;
    }

    @GetMapping("/public/responseError/b_runtimeError")
    @Operation(summary = "Runtime 에러 ", description = "")
    public Object runtimeError(@Parameter(hidden = true) @RequestHeader("X-Auth") String authHeader) {
        return authHeader;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/public/responseError/c_authError")
    @Operation(summary = "권한 에러", description = "")
    public Object authError() {
        return "authError";
    }

    @GetMapping("/public/responseError/d_serviceErrorWithDefaultMessage")
    @Operation(summary = "서비스 에러 처리 (기본 메시지)", description = "")
    public Object serviceErrorWithDefaultMessage() {
        if(true) throw new ServiceException(ServiceErrorCodeEnum.NO_RESOURCE_ERROR);
        return "serviceErrorWithDefaultMessage";
    }

    @GetMapping("/public/responseError/e_serviceErrorWithUserMessage")
    @Operation(summary = "서비스 에러 처리 (유저 메세지)", description = "")
    public Object serviceErrorWithUserMessage() {
        if (true) {
            throw new ServiceException(ServiceErrorCodeEnum.NO_RESOURCE_ERROR, "주문 내역이 없습니다.");
        }
        return "serviceErrorWithUserMessage";
    }

    @GetMapping("/public/responseError/f_badServiceErrorUsage")
    @Operation(summary = "서비스 에러 처리의 Bad 예시 (ID 중복 검사 가정)", description = "")
    public Object badServiceErrorUsage(@RequestParam String userId) {
        return responseErrorService.isAvailableId_badExample(userId);
    }

    @GetMapping("/public/responseError/g_goodServiceErrorUsage")
    @Operation(summary = "서비스 에러 처리의 Good 예시 (ID 중복 검사 가정)", description = "")
    public Object goodServiceErrorUsage(@RequestParam String userId) {
        return responseErrorService.isAvailableId_goodExample(userId);
    }

}
