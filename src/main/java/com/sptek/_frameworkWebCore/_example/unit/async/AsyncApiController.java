package com.sptek._frameworkWebCore._example.unit.async;

import com.sptek._frameworkWebCore._annotation.Enable_AsyncController_At_RestControllerMethod;
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

@Slf4j
@RequiredArgsConstructor
@RestController
@Enable_ResponseOfApiCommonSuccess_At_RestController
@Enable_ResponseOfApiGlobalException_At_RestController
@RequestMapping(value = {"/api/"}, produces = {MediaType.APPLICATION_JSON_VALUE/*, MediaType.APPLICATION_XML_VALUE*/})
@Tag(name = "Async", description = "")

public class AsyncApiController {
    private final AsyncService asyncService;

    @GetMapping(value = "/01/example/async/noReturnJob")
    @Operation(summary = "01. 동기: 응답 대기-> Worker 홀딩-> 서비스 동기 처리(return 없음)-> 응답-> Worker 반환", description = "")
    public Object noReturnJob() throws Exception {
        asyncService.noReturnJob();
        return "success";
    }

    @GetMapping(value = "/02/example/async/returnObjectJob")
    @Operation(summary = "02. 동기: 응답 대기-> Worker 홀딩-> 서비스 동기 처리(return 있음)-> 응답-> Worker 반환", description = "")
    public Object returnObjectJob() throws Exception {
        return asyncService.returnObjectJob();
    }

    @GetMapping(value = "/03/example/async/noReturnJobWithAsync")
    @Operation(summary = "03. 비동기: 바로 응답-> Worker 반환-> 서비스 비동기 처리(return 없음)", description = "")
    public Object noReturnJobWithAsync() throws Exception {
        asyncService.noReturnJobWithAsync();
        return "success";
    }

    @GetMapping(value = "/04/example/async/returnObjectJobWithAsync")
    @Operation(summary = "04. 비동기: 응답 대기-> Worker 홀딩-> 서비스 비동기 처리(return 있음)-> 응답-> Worker 반환", description = "")
    public Object returnObjectJobWithAsync()  throws Exception {
        return asyncService.returnObjectJobWithAsync();
    }

    @Enable_AsyncController_At_RestControllerMethod
    @GetMapping(value = "/05/example/async/returnObjectJobWithAsyncController")
    @Operation(summary = "05. 비동기: 응답 대기-> Worker 반환-> 서비스 비동기 처리(return 있음)-> Worker 재할당 및 응답-> Worker 반환", description = "")
    public Object returnObjectJobWithAsyncController() throws Exception {
        return asyncService.returnObjectJob();
    }

    @GetMapping(value = "/06/example/async/recommendAsyncJoin")
    @Operation(summary = "06. 비동기: 응답 대기-> Worker 홀딩-> 서비스 비동기 병렬 처리(return join)-> 응답-> Worker 반환", description = "")
    public Object recommendAsyncJoin()  throws Exception {
        return asyncService.recommendAsyncJoin();
    }

    @Enable_AsyncController_At_RestControllerMethod
    @GetMapping(value = "/07/example/async/recommendAsyncJoinWithAsyncController")
    @Operation(summary = "07. 비동기: 응답 대기-> Worker 반환-> 서비스 비동기 병렬 처리(return join)-> Worker 재할당 및 응답-> Worker 반환", description = "")
    public Object recommendAsyncJoinWithAsyncController()  throws Exception {
        return asyncService.recommendAsyncJoin();
    }

    //--------------- requestFetch.html 테스트 용 --------------------
    @GetMapping(value = "/91/example/async/justSleep1")
    @Operation(hidden = true)
    public Object justSleep1()  throws Exception {
        Thread.sleep(1000L);
        return "just sleep1";
    }

    @GetMapping(value = "/92/example/async/justSleep2")
    @Operation(hidden = true)
    public Object justSleep2()  throws Exception {
        Thread.sleep(2000L);
        return "just sleep2";
    }

    @GetMapping(value = "/93/example/async/justSleep3")
    @Operation(hidden = true)
    public Object justSleep3()  throws Exception {
        Thread.sleep(3000L);
        return "just sleep3";
    }
}
