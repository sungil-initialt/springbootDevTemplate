package com.sptek._frameworkWebCore.schedule.scheduler;

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

public class SchedulerForAsyncPoolMonitoring {
    private final ThreadPoolTaskScheduler schedulerExecutorForAsyncMonitoring;
    private final TaskExecutor  threadPoolForAsync;
    private int SCHEDULE_WITH_FIXED_DELAY_SECONDS = 6;
    private ScheduledFuture<?> scheduledFuture = null;

    public SchedulerForAsyncPoolMonitoring(
            @Qualifier("schedulerExecutorForAsyncMonitoring") ThreadPoolTaskScheduler schedulerExecutorForAsyncMonitoring,
            @Qualifier("taskExecutor") TaskExecutor threadPoolForAsync) {
        this.schedulerExecutorForAsyncMonitoring = schedulerExecutorForAsyncMonitoring;
        this.threadPoolForAsync = threadPoolForAsync;
    }

    @PostConstruct
    public void postConstruct() {
        if (scheduledFuture != null) return;
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
                logContent = String.format("poolSize=%d, activeCount=%d, queueSize(waiting)=%d, corePoolSize=%d, maxPoolSize=%d",
                        executor.getPoolSize(),
                        executor.getActiveCount(),
                        executor.getQueue().size(),
                        executor.getCorePoolSize(),
                        executor.getMaximumPoolSize());
            } else {
                logContent = "Not a ThreadPoolTaskExecutor instance: " + threadPoolForAsync.getClass().getName();
            }
            log.info(LoggingUtil.makeFwLogForm("Scheduler For Async Monitoring", logContent));

        } catch (Exception e) {
            log.warn("Scheduler For Async Monitoring", e);
        }
    }
}
