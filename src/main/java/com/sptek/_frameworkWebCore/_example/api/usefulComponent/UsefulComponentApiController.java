package com.sptek._frameworkWebCore._example.api.usefulComponent;

import com.sptek._frameworkWebCore._example.dto.ExUserDto;
import com.sptek._frameworkWebCore.annotation.EnableArgumentResolver_InParam;
import com.sptek._frameworkWebCore.annotation.EnableResponseOfApiCommonSuccess_InRestController;
import com.sptek._frameworkWebCore.annotation.EnableResponseOfApiGlobalException_InRestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@EnableResponseOfApiCommonSuccess_InRestController
@EnableResponseOfApiGlobalException_InRestController
@RequestMapping(value = {"/api/v1/example/usefulComponent/"}, produces = {MediaType.APPLICATION_JSON_VALUE/*, MediaType.APPLICATION_XML_VALUE*/})
@Tag(name = "useful Component", description = "")

public class UsefulComponentApiController {

    @PostMapping("/withoutArgumentResolver")
    @Operation(summary = "ArgumentResolver 비적용", description = "")
    public Object withoutArgumentResolver(ExUserDto exUserDto) {
        //단순히 바인딩 처리됨
        return exUserDto;
    }

    @PostMapping("/withArgumentResolver")
    @Operation(summary = "ArgumentResolver 적용", description = "")
    public Object withArgumentResolver(@EnableArgumentResolver_InParam ExUserDto exUserDto) {
        //ArgumentResolver(ExampleArgumentResolverForExUserDto) 정의에 따라 바인딩 처리됨
        return exUserDto;
    }
}
