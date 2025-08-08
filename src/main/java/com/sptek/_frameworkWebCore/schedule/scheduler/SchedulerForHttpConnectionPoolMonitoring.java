package com.sptek._frameworkWebCore.schedule.scheduler;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.ProtocolHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Component

public class SchedulerForHttpConnectionPoolMonitoring {
    private final ThreadPoolTaskScheduler schedulerExecutorForHttpConnectionPoolMonitoring;
    private TomcatWebServer  tomcatWebServer;
    private int SCHEDULE_WITH_FIXED_DELAY_SECONDS = 6;
    private ScheduledFuture<?> scheduledFuture = null;

    public SchedulerForHttpConnectionPoolMonitoring(@Qualifier("schedulerExecutorForHttpConnectionPoolMonitoring") ThreadPoolTaskScheduler schedulerExecutorForHttpConnectionPoolMonitoring) {
        this.schedulerExecutorForHttpConnectionPoolMonitoring = schedulerExecutorForHttpConnectionPoolMonitoring;
    }

    @EventListener
    public void listen(ServletWebServerInitializedEvent servletWebServerInitializedEvent) {
        if (scheduledFuture != null) return;
        if (servletWebServerInitializedEvent.getWebServer() instanceof TomcatWebServer tomcatWebServer) {
            this.tomcatWebServer = tomcatWebServer;
            scheduledFuture = schedulerExecutorForHttpConnectionPoolMonitoring.scheduleWithFixedDelay(this::doJobs, Duration.ofSeconds(SCHEDULE_WITH_FIXED_DELAY_SECONDS));
        }
    }

    // Spring 이 종료되며 해당 빈을 제거하기 전에 호출됨
    @PreDestroy
    public void preDestroy() {
        if (scheduledFuture == null) return;
        scheduledFuture.cancel(false); // 현재 작업이 끝나길 기다리고 중단
        schedulerExecutorForHttpConnectionPoolMonitoring.shutdown();
    }

    // 실제 스케줄 내용
    public void doJobs() {
        //---> 이부분 개선 부터 해야 함
        try {
            for (Connector connector : tomcatWebServer.getTomcat().getService().findConnectors()) {
                ProtocolHandler protocolHandler = connector.getProtocolHandler();
                if (protocolHandler instanceof AbstractProtocol<?> protocol) {
                    int maxThreads = protocol.getMaxThreads();
                    int currentThreads = -1;
                    int busyThreads = -1;
                    int queueSize = -1;

                    // Executor가 실제로 Tomcat ThreadPoolExecutor 타입일 때만 상태 확인
                    var executor = protocol.getExecutor();
                    if (executor instanceof org.apache.tomcat.util.threads.ThreadPoolExecutor threadPoolExecutor) {
                        currentThreads = threadPoolExecutor.getPoolSize();
                        busyThreads = threadPoolExecutor.getActiveCount();
                        queueSize = threadPoolExecutor.getQueue().size();
                    }

                    log.info("[Tomcat ThreadPool] port={}, maxThreads={}, currentThreads={}, busyThreads={}, queueSize={}",
                            //connector.getProtocol(),
                            connector.getPort(),
                            maxThreads,
                            currentThreads,
                            busyThreads,
                            queueSize
                    );
                }
            }

        } catch (Exception e) {
            log.warn("Scheduler For Async Monitoring", e);
        }
    }
}
