package com.sptek._frameworkWebCore.schedule.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class SchedulerConfigForFrameworkWebCore {

    @Bean(name = "ForPoolingHttpClientConnectionManagerMonitoring")
    public ThreadPoolTaskScheduler forPoolingHttpClientConnectionManagerMonitoring() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1); // 다른 스케줄러의 영향을 받지 않도록 한나의 전용 쓰레드로 동작
        scheduler.setThreadNamePrefix("ForPoolingHttpClientConnectionManagerMonitoring-");
        scheduler.setRemoveOnCancelPolicy(true);
        scheduler.initialize();
        return scheduler;
    }
}
