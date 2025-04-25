package com.sptek._frameworkWebCore._example.unit.usefulComponent;

import com.sptek._frameworkWebCore._example.dto.ExUserDto;
import com.sptek._frameworkWebCore.annotation.EnableArgumentResolver_InParam;
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
@RequestMapping(value = {"/api/"}, produces = {MediaType.APPLICATION_JSON_VALUE/*, MediaType.APPLICATION_XML_VALUE*/})
@Tag(name = "useful Component", description = "")

public class UsefulComponentApiController {

    @PostMapping(value = "/01/example/usefulComponent/withoutArgumentResolver", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    @Operation(summary = "01. ArgumentResolver 비적용", description = "")
    public Object withoutArgumentResolver(@ModelAttribute ExUserDto exUserDto) {
        //단순히 바인딩 처리됨
        return exUserDto;
    }

    @PostMapping(value = "/02/example/usefulComponent/withArgumentResolver", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    @Operation(summary = "02. ArgumentResolver 적용", description = "")
    public Object withArgumentResolver(@EnableArgumentResolver_InParam ExUserDto exUserDto) {
        //정의에 따라 바인딩 처리됨 ArgumentResolver(ExampleArgumentResolverForExUserDto)
        return exUserDto;
    }
}
