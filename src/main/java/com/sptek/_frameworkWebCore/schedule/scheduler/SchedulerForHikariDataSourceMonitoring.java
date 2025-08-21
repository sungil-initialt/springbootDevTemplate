package com.sptek._frameworkWebCore.schedule.scheduler;

import com.sptek._frameworkWebCore._annotation.Enable_HikariDataSourceMonitoring_At_Main;
import com.sptek._frameworkWebCore._annotation.annotationCondition.HasAnnotationOnMain_At_Bean;
import com.sptek._frameworkWebCore.base.constant.MainClassAnnotationRegister;
import com.sptek._frameworkWebCore.util.ExceptionUtil;
import com.sptek._frameworkWebCore.util.LoggingUtil;
import com.zaxxer.hikari.HikariConfigMXBean;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Component
@HasAnnotationOnMain_At_Bean(Enable_HikariDataSourceMonitoring_At_Main.class)

public class SchedulerForHikariDataSourceMonitoring {

    private final ThreadPoolTaskScheduler schedulerExecutorForHikariDataSourceMonitoring;
    private Map<String, HikariDataSource> hikariDataSources = null;
    private ScheduledFuture<?> scheduledFuture = null;
    private String logTag;

    public SchedulerForHikariDataSourceMonitoring(@Qualifier("schedulerExecutorForHikariDataSourceMonitoring") ThreadPoolTaskScheduler schedulerExecutorForHikariDataSourceMonitoring) {
        this.schedulerExecutorForHikariDataSourceMonitoring = schedulerExecutorForHikariDataSourceMonitoring;
    }

    @EventListener // 시작에 MainClassAnnotationRegister 가 필요 함으로 ContextRefreshedEvent 을 기다려 시작함
    public void listen(ContextRefreshedEvent contextRefreshedEvent) {
        if (scheduledFuture != null) return;

        hikariDataSources = contextRefreshedEvent.getApplicationContext().getBeansOfType(HikariDataSource.class);
        hikariDataSources.values().forEach(ds -> {
            // 모니터링 전 한번 강제 연결을 통해 활성화 시킴
            try (var conn = ds.getConnection()) {
            } catch (Exception e) {
                log.warn("Failed to pre-warm HikariDataSource: {}", ds, e);
            }
        });

        int SCHEDULE_WITH_FIXED_DELAY_SECONDS = 5;
        logTag = Objects.toString(MainClassAnnotationRegister.getAnnotationAttributes(Enable_HikariDataSourceMonitoring_At_Main.class).get("value"), "");
        scheduledFuture = schedulerExecutorForHikariDataSourceMonitoring.scheduleWithFixedDelay(this::doJobs, Duration.ofSeconds(SCHEDULE_WITH_FIXED_DELAY_SECONDS));
    }

    @PreDestroy
    public void preDestroy() {
        if (scheduledFuture == null) return;
        scheduledFuture.cancel(false); // 현재 작업이 끝나길 기다리고 중단
        schedulerExecutorForHikariDataSourceMonitoring.shutdown();
    }

    // 실제 스케줄 내용
    public void doJobs() {

        for (HikariDataSource hikariDataSource : hikariDataSources.values()) {
            HikariConfigMXBean hikariConfigMXBean = hikariDataSource.getHikariConfigMXBean();
            HikariPoolMXBean hikariPoolMXBean = hikariDataSource.getHikariPoolMXBean();

            String logContent = """
                   %s => DB연결(TotalConnections)=%s, 사용중(ActiveConnections)=%s, 사용가능(IdleConnections)=%s, 할당대기(ThreadsAwaitingConnection)=%s
                   [CONFIG] 최대허용(MaximumPoolSize)=%s, 상시대기(MinimumIdle)=%s, ThreadsAwaitingConnection 에서 최대 대기시간(ConnectionTimeout)=%s, 유휴 커넥션 회수 시간(IdleTimeout)=%s DB와 커넥션을 새로 연결하는 시간, DB쪽 타임아웃 보다 작게, refresh 의미 (MaxLifetime)=%s, DB 커넥션 헬스체크 타임아웃, 시간내 응답 없으면 새로 연결(ValidationTimeout)=%s
                   """
                    .formatted(
                            hikariDataSource.getPoolName()
                            , ExceptionUtil.exSafe(hikariPoolMXBean::getTotalConnections, -1)
                            , ExceptionUtil.exSafe(hikariPoolMXBean::getActiveConnections, -1)
                            , ExceptionUtil.exSafe(hikariPoolMXBean::getIdleConnections, -1)
                            , ExceptionUtil.exSafe(hikariPoolMXBean::getThreadsAwaitingConnection, -1)

                            , ExceptionUtil.exSafe(hikariConfigMXBean::getMaximumPoolSize, -1)
                            , ExceptionUtil.exSafe(hikariConfigMXBean::getMinimumIdle, -1)
                            , ExceptionUtil.exSafe(hikariConfigMXBean::getConnectionTimeout, -1)
                            , ExceptionUtil.exSafe(hikariConfigMXBean::getIdleTimeout, -1)
                            , ExceptionUtil.exSafe(hikariConfigMXBean::getMaxLifetime, -1)
                            , ExceptionUtil.exSafe(hikariConfigMXBean::getValidationTimeout, -1));


            log.info(LoggingUtil.makeBaseForm(logTag, "HikariDataSource Monitoring (Scheduler)", logContent));
        }
    }
}
