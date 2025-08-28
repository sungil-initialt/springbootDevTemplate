package com.sptek._frameworkWebCore._example.unit.deduplication;

import com.sptek._frameworkWebCore._annotation.Enable_AsyncResponse_At_RestControllerMethod;
import com.sptek._frameworkWebCore._annotation.Enable_PreventDuplicateRequest_At_RestController_RestControllerMethod;
import com.sptek._frameworkWebCore._annotation.Enable_ResponseOfApiCommonSuccess_At_RestController;
import com.sptek._frameworkWebCore._annotation.Enable_ResponseOfApiGlobalException_At_RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@Enable_ResponseOfApiCommonSuccess_At_RestController
@Enable_ResponseOfApiGlobalException_At_RestController
@RequestMapping(value = {"/api/"}, produces = {MediaType.APPLICATION_JSON_VALUE/*, MediaType.APPLICATION_XML_VALUE*/})
@Tag(name = "Prevent Duplication Request", description = "")

public class DeduplicationApiController {

    @Enable_AsyncResponse_At_RestControllerMethod
    @Enable_PreventDuplicateRequest_At_RestController_RestControllerMethod
    @RequestMapping(value = "/01/example/deduplication/preventDuplicateRequest", method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "01. 동일 요청이 빠르게 연속 요청 되는 것을 방지", description = "")
    public Object duplicatedRequest() throws Exception {
        Thread.sleep(3000L);
        return "prevent duplicateRequest test ok";
    }
}


