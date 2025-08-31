package com.sptek._frameworkWebCore._example.unit.async;

import com.sptek._frameworkWebCore._example.dto.ExUserDto;
import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import com.sptek._frameworkWebCore.util.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service

public class AsyncService {
    private final TaskExecutor taskExecutor;
    public AsyncService(@Qualifier("taskExecutor") TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public void voidLongJob() throws Exception {
        Thread.sleep(6_000L);
    }

    @Async // todo: 리턴 void 일 경우 @Async 사용이 가능하며, 별도 쓰레드를 통해 백그라운에서 처리 된다.
    public void voidLongJobWithAsync() throws Exception {
        Thread.sleep(6_000L);
    }

    //@Async // todo: 일반 object 를 리턴하는 경우 @Async 를 사용할 수 없음
    public TestDto returnLongJob() throws Exception {
        Thread.sleep(6_000L);
        return new TestDto("sungilry", 20);
    }

    @Async // todo: 리턴 객체를 CompletableFuture 로 랩핑하여 리턴하는 경우는 @Async 사용이 가능하며 별도 쓰레드에서 처리 된다.
    public CompletableFuture<TestDto> returnLongJobWithAsync() throws Exception {
        Thread.sleep(6_000L);
        return CompletableFuture.completedFuture(new TestDto("sungilry", 20));
    }

    public TestDto returnLongJobWithTaskExecutor() throws Exception {
        // Service 일부 작업을 별도 쓰레드에서 비동기 백그라운 처리가 필요할때
        taskExecutor.execute(() -> {
            try {
                TestDto result = returnLongJob();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        //해당 메소드에 @Async 가 적용되어 있음으로 알아서 별도 비동기로 동작함
        voidLongJobWithAsync();

        // 일부는 동기적으로 처리
        return new TestDto("sungilry", 20);
    }


    // Test 임시 DTO
    public record TestDto (String Name, int Age) {}
}
