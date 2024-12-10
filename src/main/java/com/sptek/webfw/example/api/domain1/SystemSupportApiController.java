package com.sptek.webfw.example.api.domain1;

import com.sptek.webfw.common.responseDto.ApiSuccessResponseDto;
import com.sptek.webfw.support.CommonControllerSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
//swagger
@Tag(name = "기본정보", description = "테스트를 위한 기본 api 그룹")
public class SystemSupportApiController extends CommonControllerSupport {

    @GetMapping("/health/healthCheck")
    @Operation(summary = "healthCheck", description = "healthCheck 테스트", tags = {""}) //swagger
    public ResponseEntity<ApiSuccessResponseDto<String>> healthCheck() {
        return ResponseEntity.ok(new ApiSuccessResponseDto<>("ok"));
    }

}
