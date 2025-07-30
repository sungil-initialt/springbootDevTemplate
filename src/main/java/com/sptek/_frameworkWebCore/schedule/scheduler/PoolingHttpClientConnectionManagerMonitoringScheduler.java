package com.sptek._frameworkWebCore.schedule.scheduler;

import com.sptek._frameworkWebCore.annotation.Enable_HttpClientMonitoringLog_At_Main;
import com.sptek._frameworkWebCore.base.constant.MainClassAnnotationRegister;
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
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Component

public class PoolingHttpClientConnectionManagerMonitoringScheduler {
    // todo: Scheduler 시작과 종료에 대해 여러 방법을 이용할 수 있다. (케이스에 적합하게 선택하여 처리할 것)
    // @PostConstruct 와 @PreDestroy 를 통해 처리할 수 있으나 처리 로직에 제 3의 Bean 을 @Lookup 이나 ApplicationContext로 가져와 사용하는 경우 해당 빈의 생존을 보장 받을 수 없다.(생성자나 @Autowired 를 통해 주입된 빈은 보장됨)
    // contextRefreshedEvent는 SmartLifecycle를 포함하는 모든 빈이 생성된 이후 발생되며 contextClosedEvent는 SmartLifecycle를 포함하는 모든 빈이 살아 있을때 먼저 발생된다.(그러나 Listener 를 따로 구현해야하는 번거러움이 있다)
    // SmartLifecycle IF 구성을 통해 컴포너트의 생명주기를 더 정교하게 조정할 수 있다 (모든 일반 빈들이 생성된 이후 생성하며 모든 빈들이 destroy 전에 destroy 할수 있고 동기/비동기로 처리가능)

    private final ThreadPoolTaskScheduler threadPoolTaskSchedulerForOutboundSupportMonitoring;
    private final PoolingHttpClientConnectionManager poolingHttpClientConnectionManager;
    private final int SCHEDULE_WITH_FIXED_DELAY_SECONDS = 6;
    private ScheduledFuture<?> scheduledFuture = null;

    // todo: @Qualifier 로 특정 빈을 주입 받을때는 @RequiredArgsConstructor 가 정상동작 하지 않을 수 있음으로 직접 생성자 구현 할것
    public PoolingHttpClientConnectionManagerMonitoringScheduler(
            @Qualifier("ThreadPoolTaskSchedulerForOutboundSupportMonitoring") ThreadPoolTaskScheduler schedulerForOutboundSupportMonitoring,
            PoolingHttpClientConnectionManager poolingHttpClientConnectionManager) {
        this.threadPoolTaskSchedulerForOutboundSupportMonitoring = schedulerForOutboundSupportMonitoring;
        this.poolingHttpClientConnectionManager = poolingHttpClientConnectionManager;
    }

    @PostConstruct
    public void postConstruct() {
        if (scheduledFuture != null) return;
        scheduledFuture = threadPoolTaskSchedulerForOutboundSupportMonitoring.scheduleWithFixedDelay(this::doJobs, Duration.ofSeconds(SCHEDULE_WITH_FIXED_DELAY_SECONDS));
    }

    // Spring 이 종료되며 해당 빈을 제거하기 전에 호출됨
    @PreDestroy
    public void preDestroy() {
        if (scheduledFuture == null) return;
        scheduledFuture.cancel(false); // 현재 작업이 끝나길 기다리고 중단
        threadPoolTaskSchedulerForOutboundSupportMonitoring.shutdown();
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

            // PoolingHttpClientMonitoring 상태 정보 로깅
            if (MainClassAnnotationRegister.hasAnnotation(Enable_HttpClientMonitoringLog_At_Main.class)) {
                String tagName = String.valueOf(MainClassAnnotationRegister.getAnnotationAttributes(Enable_HttpClientMonitoringLog_At_Main.class).get("value"));
                log.info(SptFwUtil.convertSystemNotice(tagName, "PoolingHttpClientMonitoring", logBuilder.toString()));
            }
        } catch (Exception e) {
            log.warn("Error while monitoring HttpClient Connection Pool", e);
        }
    }

    private String getRouteKey(HttpRoute route) { //로깅 보조 함수
        HttpHost targetHost = route.getTargetHost();
        return String.format("%s://%s:%d",
                targetHost.getSchemeName(),
                targetHost.getHostName(),
                targetHost.getPort());
    }
}
