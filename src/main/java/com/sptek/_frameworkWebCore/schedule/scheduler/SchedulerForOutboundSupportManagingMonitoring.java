package com.sptek._frameworkWebCore.schedule.scheduler;

import com.sptek._frameworkWebCore._annotation.Enable_OutboundSupportMonitoring_At_Main;
import com.sptek._frameworkWebCore.base.constant.MainClassAnnotationRegister;
import com.sptek._frameworkWebCore.util.LoggingUtil;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.pool.PoolStats;
import org.apache.hc.core5.util.TimeValue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Component

// 단순 상태 모니터링이 뿐만 아니라 관리를 포함함
public class SchedulerForOutboundSupportManagingMonitoring {
    // todo: @Enable_OutboundSupportMonitoring_At_Main 적용과 관련 없이 동작되며 Enable_OutboundSupportMonitoring_At_Main 단지 모니터링 로깅 조건임

    // Scheduler 시작과 종료에 대해 여러 방법을 이용할 수 있다. (케이스에 적합하게 선택하여 처리할 것)
    // @PostConstruct 와 @PreDestroy 를 통해 처리할 수 있으나 처리 로직에 제 3의 Bean 을 @Lookup 이나 ApplicationContext로 가져와 사용하는 경우 해당 빈의 생존을 보장 받을 수 없다.(생성자나 @Autowired 를 통해 주입된 빈은 보장됨)
    // contextRefreshedEvent는 SmartLifecycle를 포함하는 모든 빈이 생성된 이후 발생되며 contextClosedEvent는 SmartLifecycle를 포함하는 모든 빈이 살아 있을때 먼저 발생된다.(그러나 Listener 를 따로 구현해야하는 번거러움이 있다)
    // SmartLifecycle IF 구성을 통해 컴포너트의 생명주기를 더 정교하게 조정할 수 있다 (모든 일반 빈들이 생성된 이후 생성하며 모든 빈들이 destroy 전에 destroy 할수 있고 동기/비동기로 처리가능)

    private final ThreadPoolTaskScheduler schedulerExecutorForOutboundSupportMonitoring;
    private final PoolingHttpClientConnectionManager poolingHttpClientConnectionManager;
    private ScheduledFuture<?> scheduledFuture = null;
    private boolean has_Enable_OutboundSupportMonitoring_At_Main;
    private String logTag;

    public SchedulerForOutboundSupportManagingMonitoring(
            @Qualifier("schedulerExecutorForOutboundSupportMonitoring") ThreadPoolTaskScheduler schedulerExecutorForOutboundSupportMonitoring,
            PoolingHttpClientConnectionManager poolingHttpClientConnectionManager) {
        this.schedulerExecutorForOutboundSupportMonitoring = schedulerExecutorForOutboundSupportMonitoring;
        this.poolingHttpClientConnectionManager = poolingHttpClientConnectionManager;
    }

    @EventListener // 시작에 MainClassAnnotationRegister 가 필요 함으로 ContextRefreshedEvent 을 기다려 시작함
    public void listen(ContextRefreshedEvent contextRefreshedEvent) {
        if (scheduledFuture != null) return;
        int SCHEDULE_WITH_FIXED_DELAY_SECONDS = 10;
        has_Enable_OutboundSupportMonitoring_At_Main = MainClassAnnotationRegister.hasAnnotation(Enable_OutboundSupportMonitoring_At_Main.class);
        logTag = Objects.toString(MainClassAnnotationRegister.getAnnotationAttributes(Enable_OutboundSupportMonitoring_At_Main.class).get("value"), "");
        scheduledFuture = schedulerExecutorForOutboundSupportMonitoring.scheduleWithFixedDelay(this::doJobs, Duration.ofSeconds(SCHEDULE_WITH_FIXED_DELAY_SECONDS));
    }

    // Spring 이 종료되며 해당 빈을 제거하기 전에 호출됨
    @PreDestroy
    public void preDestroy() {
        if (scheduledFuture == null) return;
        scheduledFuture.cancel(false); // spring 종료시 진행중 작업을 cancel 하지 않고 마무리 하도록 설정
        schedulerExecutorForOutboundSupportMonitoring.shutdown();
    }

    // 실제 스케줄 내용
    public void doJobs() {
        try {
            StringBuilder logBuilder = new StringBuilder();
            PoolStats beforeStats = poolingHttpClientConnectionManager.getTotalStats();

            // 커넥션 정리 수행
            poolingHttpClientConnectionManager.closeIdle(TimeValue.ofSeconds(10));
            poolingHttpClientConnectionManager.closeExpired();
            PoolStats afterStats = poolingHttpClientConnectionManager.getTotalStats();

            logBuilder.append(String.format("사용중(Leased)=%d->%d, 사용가능(Available)=%d->%d, 대기중(Pending)=%d->%d\n"
                    , beforeStats.getLeased(), afterStats.getLeased(), beforeStats.getAvailable()
                    , afterStats.getAvailable(), beforeStats.getPending(), afterStats.getPending()));

            // 현재 각 Route별 상태
            for (HttpRoute route : poolingHttpClientConnectionManager.getRoutes()) {
                PoolStats routeStats = poolingHttpClientConnectionManager.getStats(route);
                logBuilder.append(String.format("%s => Leased=%d, Available=%d, Pending=%d\n"
                        , getRouteKey(route), routeStats.getLeased(), routeStats.getAvailable(), routeStats.getPending()));
            }

            if (has_Enable_OutboundSupportMonitoring_At_Main) {
                log.info(LoggingUtil.makeBaseForm("OutboundSupport Monitoring (Scheduler)", logBuilder.toString(), logTag));
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
