package com.sptek._frameworkWebCore.schedule.scheduler;

import com.sptek._frameworkWebCore._annotation.Enable_HttpConnectionMonitoring_At_Main;
import com.sptek._frameworkWebCore._annotation.annotationCondition.HasAnnotationOnMain_At_Bean;
import com.sptek._frameworkWebCore.base.constant.MainClassAnnotationRegister;
import com.sptek._frameworkWebCore.util.LoggingUtil;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.ProtocolHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Component
@HasAnnotationOnMain_At_Bean(Enable_HttpConnectionMonitoring_At_Main.class)

public class SchedulerForHttpConnectionMonitoring {
    // todo: 현재의 SchedulerForHttpConnectionMonitoring 는 embeeded tomcat 을 사용하는 경우만 동작함

    private final ThreadPoolTaskScheduler schedulerExecutorForHttpConnectionMonitoring;
    private TomcatWebServer  tomcatWebServer = null;
    private ScheduledFuture<?> scheduledFuture = null;
    private String logTag;

    public SchedulerForHttpConnectionMonitoring(@Qualifier("schedulerExecutorForHttpConnectionMonitoring") ThreadPoolTaskScheduler schedulerExecutorForHttpConnectionMonitoring) {
        this.schedulerExecutorForHttpConnectionMonitoring = schedulerExecutorForHttpConnectionMonitoring;
    }

    @EventListener // TomcatWebServer 를 얻기 위해 ServletWebServerInitializedEvent 를 listen 하여 가져옴
    public void listen(ServletWebServerInitializedEvent servletWebServerInitializedEvent) {
        if (servletWebServerInitializedEvent.getWebServer() instanceof TomcatWebServer tws) {
            this.tomcatWebServer = tws;
        }
    }

    @EventListener // 시작에 MainClassAnnotationRegister 가 필요 함으로 ContextRefreshedEvent 을 기다려 시작함
    public void listen(ContextRefreshedEvent contextRefreshedEvent) {
        if (scheduledFuture != null) return;
        int SCHEDULE_WITH_FIXED_DELAY_SECONDS = 10;
        logTag = Objects.toString(MainClassAnnotationRegister.getAnnotationAttributes(Enable_HttpConnectionMonitoring_At_Main.class).get("value"), "");
        scheduledFuture = schedulerExecutorForHttpConnectionMonitoring.scheduleWithFixedDelay(this::doJobs, Duration.ofSeconds(SCHEDULE_WITH_FIXED_DELAY_SECONDS));
    }

    @PreDestroy
    public void preDestroy() {
        if (scheduledFuture == null) return;
        scheduledFuture.cancel(false); // 현재 작업이 끝나길 기다리고 중단
        schedulerExecutorForHttpConnectionMonitoring.shutdown();
    }

    // 실제 스케줄 내용
    public void doJobs() {
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

                    String logContent = String.format("%s:%d => 최대허용(maxThreads)=%d, 상시대기(currentThreads)=%d, 사용중(busyThreads)=%d, 할당대기(queueSize)=%d",
                            connector.getProtocol(),
                            connector.getPort(),
                            maxThreads,
                            currentThreads,
                            busyThreads,
                            queueSize
                    );
                    log.info(LoggingUtil.makeBaseForm(logTag, "Http Connection Monitoring (Scheduler)", logContent));
                }
            }
        } catch (Exception e) {
            log.warn("Scheduler For HttpConnection Monitoring", e);
        }
    }
}
