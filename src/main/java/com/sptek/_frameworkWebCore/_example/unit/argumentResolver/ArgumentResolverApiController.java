package com.sptek._frameworkWebCore._example.unit.argumentResolver;

import com.sptek._frameworkWebCore._example.dto.ExUserDto;
import com.sptek._frameworkWebCore.annotation.Enable_ArgumentResolver_At_Param;
import com.sptek._frameworkWebCore.annotation.Enable_ResponseOfApiCommonSuccess_At_RestController;
import com.sptek._frameworkWebCore.annotation.Enable_ResponseOfApiGlobalException_At_RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@Enable_ResponseOfApiCommonSuccess_At_RestController
@Enable_ResponseOfApiGlobalException_At_RestController
@RequestMapping(value = {"/api/"}, produces = {MediaType.APPLICATION_JSON_VALUE/*, MediaType.APPLICATION_XML_VALUE*/})
@Tag(name = "Argument Resolver", description = "")

public class ArgumentResolverApiController {

    @PostMapping(value = "/01/example/argumentResolver/withoutArgumentResolver", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    @Operation(summary = "01. ArgumentResolver 비적용", description = "")
    public Object withoutArgumentResolver(@ModelAttribute ExUserDto exUserDto) {
        //단순히 바인딩 처리됨
        return exUserDto;
    }

    @PostMapping(value = "/02/example/argumentResolver/withArgumentResolver", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    @Operation(summary = "02. ArgumentResolver 적용", description = "")
    public Object withArgumentResolver(@Enable_ArgumentResolver_At_Param ExUserDto exUserDto) {
        //정의에 따라 바인딩 처리됨 ArgumentResolver(ExampleArgumentResolverForExUserDto)
        return exUserDto;
    }
}
