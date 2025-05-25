package com.sptek._frameworkWebCore._example.unit.deduplication;

import com.sptek._frameworkWebCore._example.dto.ValidatedDto;
import com.sptek._frameworkWebCore.annotation.EnablePreventDuplicateRequest_InRestController_RestControllerMethod;
import com.sptek._frameworkWebCore.annotation.EnableResponseOfApiCommonSuccess_InRestController;
import com.sptek._frameworkWebCore.annotation.EnableResponseOfApiGlobalException_InRestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@EnableResponseOfApiCommonSuccess_InRestController
@EnableResponseOfApiGlobalException_InRestController
@RequestMapping(value = {"/api/"}, produces = {MediaType.APPLICATION_JSON_VALUE/*, MediaType.APPLICATION_XML_VALUE*/})
@Tag(name = "Prevent Duplication Request", description = "")

public class DeduplicationApiController {

    @EnablePreventDuplicateRequest_InRestController_RestControllerMethod
    @RequestMapping(value = "/01/example/deduplication/preventDuplicateRequest", method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "01. 동일 요청이 빠르게 연속 요청 되는 것을 방지", description = "")
    public Object duplicatedRequest() throws Exception {
        //log.debug("AOP order : ??");
        String result = "prevent duplicateRequest test ok";
        Thread.sleep(3000L);
        return result;
    }

}


