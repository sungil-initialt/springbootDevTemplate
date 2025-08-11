package com.sptek._frameworkWebCore.schedule.scheduler;

import com.sptek._frameworkWebCore._annotation.Enable_AsyncMonitoring_At_Main;
import com.sptek._frameworkWebCore._annotation.annotationCondition.HasAnnotationOnMain_At_Bean;
import com.sptek._frameworkWebCore.base.constant.MainClassAnnotationRegister;
import com.sptek._frameworkWebCore.util.LoggingUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
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

    @PostConstruct
    public void postConstruct() {
        if (scheduledFuture != null) return;
        int SCHEDULE_WITH_FIXED_DELAY_SECONDS = 10;
        scheduledFuture = schedulerExecutorForAsyncMonitoring.scheduleWithFixedDelay(this::doJobs, Duration.ofSeconds(SCHEDULE_WITH_FIXED_DELAY_SECONDS));
    }

    // Spring 이 종료되며 해당 빈을 제거하기 전에 호출됨
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
                logContent = String.format("최대 허용 쓰레드(maxPoolSize)=%d, 상시 대기 쓰레드(corePoolSize)=%d, 현재 동작 쓰레드(activeCount)=%d, 쓰레드 할당 대기(queueSize)=%d",
                        executor.getMaximumPoolSize(),
                        executor.getCorePoolSize(),
                        executor.getActiveCount(),
                        executor.getQueue().size()
                );
            } else {
                logContent = "Not a ThreadPoolTaskExecutor instance: " + threadPoolForAsync.getClass().getName();
            }
            String logTag = String.valueOf(MainClassAnnotationRegister.getAnnotationAttributes(Enable_AsyncMonitoring_At_Main.class).get("value"));
            log.info(LoggingUtil.makeFwLogForm("Scheduler For Async Monitoring", logContent, logTag));

        } catch (Exception e) {
            log.warn("Scheduler For Async Monitoring", e);
        }
    }
}
