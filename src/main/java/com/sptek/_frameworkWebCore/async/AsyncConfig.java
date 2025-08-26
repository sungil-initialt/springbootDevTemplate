package com.sptek._frameworkWebCore.async;

import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableAsync
@RequiredArgsConstructor

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
        threadPoolTaskExecutor.setTaskDecorator(new RequestContextTaskDecorator()); // 중요! 하위 쓰레드 내에서도 RequestContextHolder 를 사용할 수 있도록
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

    // 하위 쓰레드에서도 RequestContextHolder 를 유지하기 위한 설정 처리
    public class RequestContextTaskDecorator implements TaskDecorator {
        @Override
        public @NotNull Runnable decorate(Runnable runnable) {
            // 호출 시점(요청 스레드)의 컨텍스트 캡처
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            var mdc = MDC.getCopyOfContextMap();

            return () -> {
                try {
                    // 백그라운드 스레드에 컨텍스트 주입
                    RequestContextHolder.setRequestAttributes(requestAttributes);
                    if (mdc != null) MDC.setContextMap(mdc);
                    runnable.run();
                } finally {
                    // 누수 방지
                    RequestContextHolder.resetRequestAttributes();
                    MDC.clear();
                }
            };
        }
    }

    @Bean
    public BeanPostProcessor prioritizeMyHandler(ControllerReturnValueHandlerForAsync controllerReturnValueHandlerForAsync) {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) {
                if (bean instanceof RequestMappingHandlerAdapter adapter) {
                    List<HandlerMethodReturnValueHandler> existing = adapter.getReturnValueHandlers();
                    if (existing == null) return bean;

                    List<HandlerMethodReturnValueHandler> reordered = new ArrayList<>(existing);
                    // 중복 방지
                    reordered.removeIf(h -> h.getClass() == controllerReturnValueHandlerForAsync.getClass());
                    // 맨 앞에 삽입
                    reordered.add(0, controllerReturnValueHandlerForAsync);
                    adapter.setReturnValueHandlers(reordered);
                }
                return bean;
            }
        };
    }
}
