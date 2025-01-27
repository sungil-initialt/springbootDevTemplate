package com.sptek._frameworkWebCore.example.api.domain1;

import com.sptek._frameworkWebCore.annotation.EnableApiCommonSuccessResponse;
import com.sptek._frameworkWebCore.annotation.EnableApiCommonErrorResponse;
import com.sptek._frameworkWebCore.support.CommonControllerSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@EnableApiCommonSuccessResponse
@EnableApiCommonErrorResponse
@Tag(name = "기본정보", description = "테스트를 위한 기본 api 그룹") //swagger
public class SystemSupportApiController extends CommonControllerSupport {

    //해당 매핑은 NotEssentialRequestPattern 에 포함되어 있음 (필터 적용이 되지 않는다.)
    @GetMapping("/health/healthCheck")
    @Operation(summary = "healthCheck", description = "healthCheck 테스트", tags = {""}) //swagger
    public Object healthCheck() {
        return "ok";
    }

}
