package com.sptek._frameworkWebCore.schedule.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class SchedulerConfigForFrameworkWebCore {

    @Bean(name = "ThreadPoolTaskSchedulerForOutboundSupportMonitoring")
    public ThreadPoolTaskScheduler threadPoolTaskSchedulerForOutboundSupportMonitoring() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1); // 다른 Task에 영향을 받지 않도록 전용 Thread로 동작(전용 Thread의 pool은 1개로 처리)
        scheduler.setThreadNamePrefix("OutboundSupportMonitoring-");
        scheduler.setRemoveOnCancelPolicy(false); //true 스케줄된 작업이 취소되었을 때, 작업 큐에서 해당 작업을 즉시 제거하도록 설정
        scheduler.initialize();
        return scheduler;
    }
}
