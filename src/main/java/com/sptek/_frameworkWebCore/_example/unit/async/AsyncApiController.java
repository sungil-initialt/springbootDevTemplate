package com.sptek._frameworkWebCore._example.unit.async;

import com.sptek._frameworkWebCore._annotation.Enable_ResponseOfApiCommonSuccess_At_RestController;
import com.sptek._frameworkWebCore._annotation.Enable_ResponseOfApiGlobalException_At_RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@RestController
@Enable_ResponseOfApiCommonSuccess_At_RestController
@Enable_ResponseOfApiGlobalException_At_RestController
@RequestMapping(value = {"/api/"}, produces = {MediaType.APPLICATION_JSON_VALUE/*, MediaType.APPLICATION_XML_VALUE*/})
@Tag(name = "Async", description = "")

public class AsyncApiController {
    private final AsyncService asyncService;

    @GetMapping(value = "/01/example/async/case1")
    @Operation(summary = "01. Async 하지 않은 5s 작업, 응답 대기, Sever worker Thread 홀딩", description = "")
    public Object case11() throws Exception {
        asyncService.justSleep5s();
        return "ok";
    }

    @GetMapping(value = "/02/example/async/case2")
    @Operation(summary = "02. 요청을 받아 바로 응답, Sever worker Thread Release, 실제 동작은 새 Thread로 실행, 실행 결과는 리턴 하지 않음", description = "")
    public Object case2() throws Exception {
        asyncService.justSleep5sWithAsync();
        return "ok";
    }

    @GetMapping(value = "/03/example/async/case3")
    @Operation(summary = "03. 요청을 받아 바로 응답, Sever worker Thread Release, 실제 동작은 새 Thread로 실행, 처리 후 응답", description = "")
    public Object case3()  throws Exception {
        return asyncService.getUserAsync()
                .orTimeout(10, TimeUnit.SECONDS)
                .exceptionally(ex -> {throw new RuntimeException(ex);});
    }

    // for just Test ---------------------------------------------------------------------------------------------------
    @GetMapping(value = "/91/example/async/justSleep1")
    @Operation(summary = "91. 1초 sleep 후 응답", description = "")
    public Object justSleep1() throws Exception {
        Thread.sleep(1000L);
        return "justSleep 1s ok";
    }

    @GetMapping(value = "/92/example/async/justSleep2")
    @Operation(summary = "92. 2초 sleep 후 응답", description = "")
    public Object justSleep2() throws Exception {
        Thread.sleep(2000L);
        return "justSleep 2s ok";
    }

    @GetMapping(value = "/93/example/async/justSleep3")
    @Operation(summary = "93. 3초 sleep 후 응답", description = "")
    public Object justSleep3() throws Exception {
        Thread.sleep(3000L);
        return "justSleep 3s ok";
    }
}
