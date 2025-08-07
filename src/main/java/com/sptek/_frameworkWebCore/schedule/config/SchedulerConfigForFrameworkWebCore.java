package com.sptek._frameworkWebCore.schedule.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class SchedulerConfigForFrameworkWebCore {

    @Bean(name = "threadPoolForOutboundSupportMonitoring")
    public ThreadPoolTaskScheduler threadPoolForOutboundSupportMonitoring() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1); // 다른 Task에 영향을 받지 않도록 전용 Thread로 동작(전용 Thread의 pool은 1개로 처리)
        scheduler.setThreadNamePrefix("from threadPoolForOutboundSupportMonitoring-");
        scheduler.setRemoveOnCancelPolicy(false); //true 스케줄된 작업이 취소되었을 때, 작업 큐에서 해당 작업을 즉시 제거하도록 설정
        scheduler.initialize();
        return scheduler;
    }

    @Bean(name = "threadPoolForThreadPoolForAsyncAnnotationMonitoring") //ThreadPool을 모니터링하기 위한 ThreadPool 임..
    public ThreadPoolTaskScheduler threadPoolForThreadPoolForAsyncAnnotationMonitoring() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("from threadPoolForThreadPoolForAsyncAnnotationMonitoring-");
        scheduler.setRemoveOnCancelPolicy(false);
        scheduler.initialize();
        return scheduler;
    }  //-----> 이거에 대한 스케줄러 구성해야 함
}
