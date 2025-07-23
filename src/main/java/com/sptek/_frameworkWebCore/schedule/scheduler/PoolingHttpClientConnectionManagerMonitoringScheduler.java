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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component

public class PoolingHttpClientConnectionManagerMonitoringScheduler {
    private final ThreadPoolTaskScheduler schedulerForOutboundSupportMonitoring;
    private final PoolingHttpClientConnectionManager connectionManager;

    // todo: @Qualifier 로 특정 빈을 주입 받을때는 @RequiredArgsConstructor 가 정상동작 하지 않을 수 있음으로 직접 생성자 구현 할것
    public PoolingHttpClientConnectionManagerMonitoringScheduler(
            @Qualifier("SchedulerForOutboundSupportMonitoring") ThreadPoolTaskScheduler schedulerForOutboundSupportMonitoring,
            PoolingHttpClientConnectionManager connectionManager) {
        this.schedulerForOutboundSupportMonitoring = schedulerForOutboundSupportMonitoring;
        this.connectionManager = connectionManager;
    }

    // Bean 생성 이후 호출 이기 때문에 내부 로직에서 다른 Bean을 사용해야 하는 경우는 적합하지 않을 수 있음(다른 빈이 생성 이전 일 수 있음)
    // 그런 경우는 EventListener을 구현해서 ApplicationContext 의 ContextRefreshedEvent 를 받아 처리하도록 해야함
    @PostConstruct
    public void init() {
        // 작업1
        schedulerForOutboundSupportMonitoring.scheduleWithFixedDelay(() -> {
            StringBuilder logBuilder = new StringBuilder();

            try {
                // --- 상태 캡처: 정리 전 ---
                PoolStats beforeStats = connectionManager.getTotalStats();
                logBuilder.append(String.format("[Before]\nLeased: %d, Available: %d, Pending: %d\n"
                        , beforeStats.getLeased(), beforeStats.getAvailable(), beforeStats.getPending()));

                // --- 각 Route별 상태 캡처: 정리 전 ---
                for (HttpRoute route : connectionManager.getRoutes()) {
                    PoolStats routeStats = connectionManager.getStats(route);
                    logBuilder.append(String.format(
                            "[%s] Leased: %d, Available: %d, Pending: %d\n",
                            getRouteKey(route),
                            routeStats.getLeased(), routeStats.getAvailable(), routeStats.getPending()
                    ));
                }

                // --- 커넥션 정리 수행 ---
                connectionManager.closeIdle(TimeValue.ofSeconds(10));
                connectionManager.closeExpired();
                logBuilder.append("[After]\n");

                // --- 각 Route별 상태 캡처: 정리 후 ---
                for (HttpRoute route : connectionManager.getRoutes()) {
                    PoolStats routeStats = connectionManager.getStats(route);
                    logBuilder.append(String.format(
                            "[%s] Leased: %d, Available: %d, Pending: %d\n",
                            getRouteKey(route),
                            routeStats.getLeased(), routeStats.getAvailable(), routeStats.getPending()
                    ));
                }

                // --- 상태 캡처: 정리 후 ---
                PoolStats afterStats = connectionManager.getTotalStats();
                logBuilder.append(String.format("Leased: %d, Available: %d, Pending: %d\n"
                        , afterStats.getLeased(), afterStats.getAvailable(), afterStats.getPending()));

                log.info(SptFwUtil.convertSystemNotice(this.getClass().getSimpleName(), "PoolingHttpClientMonitoring", logBuilder.toString()));
            } catch (Exception e) {
                log.warn("Error while monitoring HttpClient Connection Pool", e);
            }
        }, Duration.ofSeconds(6));
        // do more.. 작업2, 3..
    }

    // Spring 이 종료되며 해당 빈을 제거하기 전에 호출됨
    @PreDestroy
    public void destroy() {
        // 명시적으로 scheduler 의 작업을 shutdown 함으로써 진행 중이던 task 까지는 안전히 진행 됨(본 스케줄 기능상 그냥 죽어도 상관은 없음)
        schedulerForOutboundSupportMonitoring.shutdown();

    }

    private String getRouteKey(HttpRoute route) {
        // 단순 로깅 보조
        HttpHost targetHost = route.getTargetHost();
        return String.format("%s://%s:%d",
                targetHost.getSchemeName(),
                targetHost.getHostName(),
                targetHost.getPort());
    }
}
