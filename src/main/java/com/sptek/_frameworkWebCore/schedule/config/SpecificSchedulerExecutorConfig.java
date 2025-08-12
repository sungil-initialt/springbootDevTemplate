package com.sptek._frameworkWebCore.schedule.config;

import com.sptek._frameworkWebCore._annotation.Enable_AsyncMonitoring_At_Main;
import com.sptek._frameworkWebCore._annotation.Enable_HttpConnectionMonitoring_At_Main;
import com.sptek._frameworkWebCore._annotation.Enable_OutboundSupportMonitoring_At_Main;
import com.sptek._frameworkWebCore._annotation.annotationCondition.HasAnnotationOnMain_At_Bean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.time.Duration;

@Slf4j
@Configuration
@RequiredArgsConstructor

public class SpecificSchedulerExecutorConfig {
    // FW 의 Scheduler 는 보다 안정적인 동작을 위해 @Scheduled 에서 사용하는 공용 ThreadPoolTaskScheduler 를 사용하지 않고
    // 각각 독립된 별도의 ThreadPoolTaskScheduler 를 생성하여 사용함

    final private Environment environment;

    @HasAnnotationOnMain_At_Bean(Enable_HttpConnectionMonitoring_At_Main.class)
    @Bean(name = "schedulerExecutorForHttpConnectionMonitoring")
    public ThreadPoolTaskScheduler schedulerExecutorForHttpConnectionMonitoring() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1); // 다른 Task에 영향을 받지 않도록 전용 Thread로 동작(전용 Thread의 pool은 1개로 처리)
        scheduler.setThreadNamePrefix("from schedulerExecutorForHttpConnectionMonitoring-");
        scheduler.setRemoveOnCancelPolicy(true); //true = 스케줄 작업이 cancel() 시 대기큐 바로 삭제
        scheduler.setWaitForTasksToCompleteOnShutdown(true); // spring 종료시 진행중 작업 마무리 가능하도록 설정
        scheduler.setAwaitTerminationSeconds((int)environment.getProperty(
                "spring.lifecycle.timeout-per-shutdown-phase", Duration.class, Duration.ofSeconds(30)).getSeconds()); // 마무리 작업 최대 보장 시간
        return scheduler;
    }

    @HasAnnotationOnMain_At_Bean(Enable_AsyncMonitoring_At_Main.class)
    @Bean(name = "schedulerExecutorForAsyncMonitoring")
    public ThreadPoolTaskScheduler schedulerExecutorForAsyncMonitoring() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("from schedulerExecutorForAsyncMonitoring-");
        scheduler.setRemoveOnCancelPolicy(true);
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds((int)environment.getProperty(
                "spring.lifecycle.timeout-per-shutdown-phase", Duration.class, Duration.ofSeconds(30)).getSeconds());
        return scheduler;
    }

    @HasAnnotationOnMain_At_Bean(Enable_OutboundSupportMonitoring_At_Main.class)
    @Bean(name = "schedulerExecutorForOutboundSupportMonitoring")
    public ThreadPoolTaskScheduler schedulerExecutorForOutboundSupportMonitoring() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("from schedulerExecutorForOutboundSupportMonitoring-");
        scheduler.setRemoveOnCancelPolicy(true);
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds((int)environment.getProperty(
                "spring.lifecycle.timeout-per-shutdown-phase", Duration.class, Duration.ofSeconds(30)).getSeconds());
        return scheduler;
    }

}
