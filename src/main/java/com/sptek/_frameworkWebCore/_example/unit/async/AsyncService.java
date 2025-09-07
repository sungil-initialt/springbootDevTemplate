package com.sptek._frameworkWebCore._example.unit.async;

import com.sptek._frameworkWebCore.base.exception.ServiceException;
import com.sptek._frameworkWebCore.util.AuthenticationUtil;
import com.sptek._frameworkWebCore.util.LocaleUtil;
import com.sptek._frameworkWebCore.util.SecurityUtil;
import com.sptek._frameworkWebCore.util.Timer;
import com.sptek._projectCommon.commonObject.code.ServiceErrorCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.format.datetime.standard.DateTimeContextHolder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service

public class AsyncService {
    // sptTaskExecutor 를 사용 해야만 하위 쓰레드 내에서도 ThreadLocal 기반의 중요 context(mdc, request, locale, dateTime) 를 사용할 수 있음
    private final TaskExecutor taskExecutor;
    public AsyncService(@Qualifier("sptTaskExecutor") TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    // 리턴 없는 일반 메소드
    public void noReturnJob() {
        Timer.sleep(10_000L);
        if (false) throw new RuntimeException("returnObjectJob RuntimeException");
        log.debug("noReturnJob done");

        // 별도 쓰레드 동작시에도 MDC, Locale, Security, DateTime 등 다양한 ContextHolder 를 그대로 이관 받음
        String userEmail = AuthenticationUtil.getMyEmail();
        String userLanguageTag = LocaleUtil.getCurUserLanguageTag();
        String userTimeZone = LocaleUtil.getCurUserTimeZoneName();
        log.debug("userLanguageTag: {}, userTimeZone: {}", userLanguageTag, userTimeZone);
    }

    // 리턴 타입이 Future 타입이 아님으로 @Async 탈부착 불가
    public AsyncDto returnObjectJob() {
        Timer.sleep(10_000L);
        if (false) throw new ServiceException(ServiceErrorCodeEnum.NO_RESOURCE_ERROR, "처리할 작업이 없습니다");
        return new AsyncDto("returnObjectJob", "success");
    }

    // 리턴이 없는 경우 @Async 탈부착 가능 (*** 비추천: 같은 클레스에서 호출하는 self-invocation 불가, EX 전파 복잡)
    @Async("sptTaskExecutor")
    public void noReturnJobWithAsync() {
        Timer.sleep(10_000L);
        if (false) throw new ServiceException(ServiceErrorCodeEnum.NO_RESOURCE_ERROR, "noReturnJobWithAsync ServiceException");
        log.debug("noReturnJobWithAsync done");
    }

    // 리턴 타입이 Future 타입 임으로 @Async 탈부착 가능 (***비추천: 같은 클레스에서 호출하는 self-invocation 불가 및 @Async 탈부착에 따라 리턴 타입이 변경 필요, EX 전파 복잡)
    @Async("sptTaskExecutor")
    public CompletableFuture<AsyncDto> returnObjectJobWithAsync() {
        Timer.sleep(10_000L);
        if (false) throw new ServiceException(ServiceErrorCodeEnum.NO_RESOURCE_ERROR, "처리할 작업이 없습니다.");
        return CompletableFuture.completedFuture(new AsyncDto("returnObjectJobWithAsync", "success"));
    }

    // 추천 Async 처리 및 병렬 작업 예시
    public List<AsyncDto> recommendAsyncJoin() throws Exception {
        // CompletableFuture 와 taskExecutor 를 통해 Sync(return) 메소드를 Async 형태로 호출
        var noReturnJob = CompletableFuture.runAsync(this::noReturnJob, taskExecutor);
        var returnObjectJob = CompletableFuture.supplyAsync(this::returnObjectJob, taskExecutor);
        var composeJob = CompletableFuture.supplyAsync(() -> {
            Timer.sleep(10_000L);
            if (false) throw new ServiceException(ServiceErrorCodeEnum.NO_RESOURCE_ERROR, "처리할 작업이 없습니다.");
            return new AsyncDto("composeJob", "success");}, taskExecutor).whenComplete((r, e) -> {/*후처리*/});

        // Async Exception 체크가 되려면 join 또는 get 이 반드시 필요 (리턴이 없어도 필요)
        noReturnJob.join();

        // Async 리턴 값 조합 처리
        var asyncDtos = new ArrayList<AsyncDto>();
        asyncDtos.add(returnObjectJob.get());
        asyncDtos.add(composeJob.get());
        return asyncDtos;
    }

    // Test 임시 DTO
    public record AsyncDto (String whoReturn, String returnValue) {}
}
