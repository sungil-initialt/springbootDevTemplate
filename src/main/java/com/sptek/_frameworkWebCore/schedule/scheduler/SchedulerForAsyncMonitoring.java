package com.sptek._frameworkWebCore.schedule.scheduler;

import com.sptek._frameworkWebCore._annotation.Enable_AsyncMonitoring_At_Main;
import com.sptek._frameworkWebCore._annotation.annotationCondition.HasAnnotationOnMain_At_Bean;
import com.sptek._frameworkWebCore.base.constant.MainClassAnnotationRegister;
import com.sptek._frameworkWebCore.util.LoggingUtil;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Component
@HasAnnotationOnMain_At_Bean(Enable_AsyncMonitoring_At_Main.class)

public class SchedulerForAsyncMonitoring {
    // Async Pool 을 모니터링 할뿐 @Enable_AsyncMonitoring_At_Main 가 적용되지 않아도 Async Pool 은 동작함
    
    private final ThreadPoolTaskScheduler schedulerExecutorForAsyncMonitoring;
    private final TaskExecutor  threadPoolForAsync;
    private ScheduledFuture<?> scheduledFuture = null;

    public SchedulerForAsyncMonitoring(
            @Qualifier("schedulerExecutorForAsyncMonitoring") ThreadPoolTaskScheduler schedulerExecutorForAsyncMonitoring,
            @Qualifier("taskExecutor") TaskExecutor threadPoolForAsync) {
        this.schedulerExecutorForAsyncMonitoring = schedulerExecutorForAsyncMonitoring;
        this.threadPoolForAsync = threadPoolForAsync;
    }

    @EventListener // @PostConstruct 시점에는 MainClassAnnotationRegister 가 생성되기 전임으로  Event Listen 방식으로 변경함
    public void listen(ContextRefreshedEvent contextRefreshedEvent) {
        if (scheduledFuture != null) return;
        int SCHEDULE_WITH_FIXED_DELAY_SECONDS = 10;
        scheduledFuture = schedulerExecutorForAsyncMonitoring.scheduleWithFixedDelay(this::doJobs, Duration.ofSeconds(SCHEDULE_WITH_FIXED_DELAY_SECONDS));
    }

    @PreDestroy
    public void preDestroy() {
        if (scheduledFuture == null) return;
        scheduledFuture.cancel(false); // 현재 작업이 끝나길 기다리고 중단
        schedulerExecutorForAsyncMonitoring.shutdown();
    }

    // 실제 스케줄 내용
    public void doJobs() {
        try {
            String logContent;
            if (threadPoolForAsync instanceof ThreadPoolTaskExecutor threadPoolTaskExecutor) {
                ThreadPoolExecutor executor = threadPoolTaskExecutor.getThreadPoolExecutor();
                logContent = String.format("최대허용(maxPoolSize)=%d, 상시대기(corePoolSize)=%d, 사용중(activeCount)=%d, 할당대기(queueSize)=%d",
                        executor.getMaximumPoolSize(),
                        executor.getCorePoolSize(),
                        executor.getActiveCount(),
                        executor.getQueue().size()
                );
            } else {
                logContent = "Not a ThreadPoolTaskExecutor instance: " + threadPoolForAsync.getClass().getName();
            }
            String logTag = Objects.toString(MainClassAnnotationRegister.getAnnotationAttributes(Enable_AsyncMonitoring_At_Main.class).get("value"), "");
            log.info(LoggingUtil.makeFwLogForm("Async Monitoring (Scheduler)", logContent, logTag));

        } catch (Exception e) {
            log.warn("Scheduler For Async Monitoring", e);
        }
    }
}
