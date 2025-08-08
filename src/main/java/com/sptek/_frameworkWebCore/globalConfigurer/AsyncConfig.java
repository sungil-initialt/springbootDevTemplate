package com.sptek._frameworkWebCore.globalConfigurer;

import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor") // name값 변경 하지 말것!
    public TaskExecutor threadPoolForAsync() {
        // todo: bean name 을 "taskExecutor" 로 지정하여 @Async 어노테이션의 기본 threadPool 로 동작하도록 처리함
        //  별도 이름 사용시 @Async("name") 형태로 사용해야 하며 별도의 pool 에서 관리됨

        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(CommonConstants.RECOMMEND_THREAD_POOL_SIZE); // 기본 쓰레드 수
        threadPoolTaskExecutor.setMaxPoolSize(CommonConstants.RECOMMEND_THREAD_POOL_MAX_SIZE); // 최대 쓰레드 수
        threadPoolTaskExecutor.setQueueCapacity(CommonConstants.RECOMMEND_THREAD_QUEUE_SIZE); // 대기 큐 크기
        threadPoolTaskExecutor.setThreadNamePrefix("from threadPoolForAsync-");
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
}
