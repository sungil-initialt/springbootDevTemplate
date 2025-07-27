package com.sptek._frameworkWebCore.schedule.scheduler;

import com.sptek._frameworkWebCore.util.SptFwUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.pool.PoolStats;
import org.apache.hc.core5.util.TimeValue;
import org.jetbrains.annotations.Async;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.SmartLifecycle;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Component

public class PoolingHttpClientConnectionManagerMonitoringScheduler implements SmartLifecycle {
    // todo: Scheduler 시작과 종료에 대해 여러 방법을 이용할 수 있다.
    // 가장 안정적인 방법은 SmartLifecycle IF 구성을 통해 스케줄러의 안정적인 시작과 종료를 보장 할 수 있게 할 수 있다.
    // @PostConstruct 와 @PreDestroy 를 통해 시작과 종료를 처리할 수 있으나 처리 로직에 제 3의 Bean 을 사용 한다면 보장 받을 수 없다.(다른 빈이 생성 이전 이거나 이미 종료 되었을 수 있음)
    // @EventListener 를 통해 contextRefreshedEvent 에서 처리 할수도 있다

    private final ThreadPoolTaskScheduler threadPoolTaskSchedulerForOutboundSupportMonitoring;
    private final PoolingHttpClientConnectionManager poolingHttpClientConnectionManager;
    private final int SCHEDULE_WITH_FIXED_DELAY_SECONDS = 6;
    private ScheduledFuture<?> scheduledFuture;
    private volatile boolean isRunning = false;

    // todo: @Qualifier 로 특정 빈을 주입 받을때는 @RequiredArgsConstructor 가 정상동작 하지 않을 수 있음으로 직접 생성자 구현 할것
    public PoolingHttpClientConnectionManagerMonitoringScheduler(
            @Qualifier("ThreadPoolTaskSchedulerForOutboundSupportMonitoring") ThreadPoolTaskScheduler schedulerForOutboundSupportMonitoring,
            PoolingHttpClientConnectionManager poolingHttpClientConnectionManager) {
        this.threadPoolTaskSchedulerForOutboundSupportMonitoring = schedulerForOutboundSupportMonitoring;
        this.poolingHttpClientConnectionManager = poolingHttpClientConnectionManager;
    }

    @Override
    // todo: spring 이 start() 호출전 isAutoStartup() 을 확인하여 true 일 star() 해준다.
    // 이미 다른 일반 Bean 은 로딩된 상태 이기 때문에 isAutoStartup() 내부 로직 구현을 통해 동적 실행을 구성 할 수 있다.
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    // todo: // SmartLifecycle 구성 Bean들 간 phase가 낮을수록 먼저 start 되고 나중에 stop 됨
    public int getPhase() {
        return 0;
    }

    @Override
    public void start() {
        scheduledFuture = threadPoolTaskSchedulerForOutboundSupportMonitoring.scheduleWithFixedDelay(this::doJobs, Duration.ofSeconds(SCHEDULE_WITH_FIXED_DELAY_SECONDS));
        isRunning = true;
    }

    @Override
    // spring 이 호출해 주는 실제 stop
    public void stop(Runnable callback) {
        stop(); // 실제 종료처리가 메소드, callback.run(); 과 순서 변경기 비동기적 종료 처리 가능(그럴경우 다른 bean의 상태를 보장 받을 수 없음)
        callback.run(); // 반드시 호출되어야 Spring context 가 정상 종료됨
    }

    @Override
    public void stop() {
        if (scheduledFuture != null) scheduledFuture.cancel(false); // 현재 작업이 끝나길 기다리고 중단
        threadPoolTaskSchedulerForOutboundSupportMonitoring.shutdown();
        isRunning = false;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    // 실제 스케줄 내용
    public void doJobs() {
        try {
            StringBuilder logBuilder = new StringBuilder();

            // --- 상태 캡처: 정리 전 ---
            PoolStats beforeStats = poolingHttpClientConnectionManager.getTotalStats();
            logBuilder.append(String.format("[Before]\nLeased: %d, Available: %d, Pending: %d\n"
                    , beforeStats.getLeased(), beforeStats.getAvailable(), beforeStats.getPending()));

            // --- 각 Route별 상태 캡처: 정리 전 ---
            for (HttpRoute route : poolingHttpClientConnectionManager.getRoutes()) {
                PoolStats routeStats = poolingHttpClientConnectionManager.getStats(route);
                logBuilder.append(String.format(
                        "[%s] Leased: %d, Available: %d, Pending: %d\n",
                        getRouteKey(route),
                        routeStats.getLeased(), routeStats.getAvailable(), routeStats.getPending()
                ));
            }

            // --- 커넥션 정리 수행 ---
            poolingHttpClientConnectionManager.closeIdle(TimeValue.ofSeconds(10));
            poolingHttpClientConnectionManager.closeExpired();
            logBuilder.append("[After]\n");

            // --- 각 Route별 상태 캡처: 정리 후 ---
            for (HttpRoute route : poolingHttpClientConnectionManager.getRoutes()) {
                PoolStats routeStats = poolingHttpClientConnectionManager.getStats(route);
                logBuilder.append(String.format(
                        "[%s] Leased: %d, Available: %d, Pending: %d\n",
                        getRouteKey(route),
                        routeStats.getLeased(), routeStats.getAvailable(), routeStats.getPending()
                ));
            }

            // --- 상태 캡처: 정리 후 ---
            PoolStats afterStats = poolingHttpClientConnectionManager.getTotalStats();
            logBuilder.append(String.format("Leased: %d, Available: %d, Pending: %d\n"
                    , afterStats.getLeased(), afterStats.getAvailable(), afterStats.getPending()));

            log.info(SptFwUtil.convertSystemNotice(this.getClass().getSimpleName(), "PoolingHttpClientMonitoring", logBuilder.toString()));
        } catch (Exception e) {
            log.warn("Error while monitoring HttpClient Connection Pool", e);
        }
    }

    private String getRouteKey(HttpRoute route) {
        // 단순 로깅요 보조 함수
        HttpHost targetHost = route.getTargetHost();
        return String.format("%s://%s:%d",
                targetHost.getSchemeName(),
                targetHost.getHostName(),
                targetHost.getPort());
    }
}
