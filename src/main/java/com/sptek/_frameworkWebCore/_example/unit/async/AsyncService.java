package com.sptek._frameworkWebCore._example.unit.async;

import com.sptek._frameworkWebCore.util.ExecutionTimer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
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
    public void voidJob() {
        ExecutionTimer.sleep(10_000L);
        log.debug("voidJob done");
    }

    // 리턴이 없는 경우 @Async 탈부착 가능 (*** 비추천: 같은 클레스에서 호출하는 self-invocation 불가)
    @Async("sptTaskExecutor")
    public void voidJobWithAsync() {
        ExecutionTimer.sleep(10_000L);
        log.debug("voidJobWithAsync done");
    }

    // 리턴 타입이 Future 타입이 아님으로 @Async 탈부착 불가
    public TestDto returnJob() {
        ExecutionTimer.sleep(10_000L);
        return new TestDto("returnJob", "success");
    }

    // 리턴 타입이 Future 타입 임으로 @Async 탈부착 가능 (***비추천: 같은 클레스에서 호출하는 self-invocation 불가 및 @Async 탈부착에 따라 리턴 타입이 변경 필요)
    @Async("sptTaskExecutor")
    public CompletableFuture<TestDto> returnJobWithAsync() {
        ExecutionTimer.sleep(10_000L);
        return CompletableFuture.completedFuture(new TestDto("returnJobWithAsync", "success"));
    }

    // 추천 Async 처리 및 병렬 작업 예시
    public List<TestDto> recommendAsyncJoin() throws Exception {
        // 1. taskExecutor 를 통해 Sync 메소드를(void) Async 형태로 호출
        taskExecutor.execute(this::voidJob);

        // 2. taskExecutor 를 통해 작업을(void) Async 로 직접 구현
        taskExecutor.execute(() -> {
            ExecutionTimer.sleep(10_000L);
            log.debug("self working(void) done");
        });

        // 3. CompletableFuture 와 taskExecutor 를 통해 Sync(return) 메소드를 Async 형태로 호출
        var result1 = CompletableFuture.supplyAsync(this::returnJob, taskExecutor);

        // 4. CompletableFuture 와 taskExecutor 를 작업을(return) Async 로 직접 구현
        var result2 = CompletableFuture.supplyAsync(() -> {
            ExecutionTimer.sleep(10_000L);
            return new TestDto("self working(return)", "success");
        }, taskExecutor);

        // Async 리턴 값 조합 처리
        var testDtos = new ArrayList<TestDto>();
        testDtos.add(result1.get());
        testDtos.add(result2.get());
        return testDtos;
    }

    // Test 임시 DTO
    public record TestDto (String whoReturn, String returnValue) {}
}
