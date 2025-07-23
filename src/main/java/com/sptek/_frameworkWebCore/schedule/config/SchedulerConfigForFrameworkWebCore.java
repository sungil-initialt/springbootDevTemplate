package com.sptek._frameworkWebCore.schedule.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class SchedulerConfigForFrameworkWebCore {

    @Bean(name = "SchedulerForOutboundSupportMonitoring")
    public ThreadPoolTaskScheduler schedulerForOutboundSupportMonitoring() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1); // 다른 Task에 영향을 받지 않도록 전용 Thread로 동작(전용 Thread의 pool은 1개로 처리)
        scheduler.setThreadNamePrefix("SchedulerForOutboundSupportMonitoring-");
        scheduler.setRemoveOnCancelPolicy(true);
        scheduler.initialize();
        return scheduler;
    }
}
