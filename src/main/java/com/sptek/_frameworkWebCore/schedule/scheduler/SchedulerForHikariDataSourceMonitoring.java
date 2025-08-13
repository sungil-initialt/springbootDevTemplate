package com.sptek._frameworkWebCore.schedule.scheduler;

import com.sptek._frameworkWebCore._annotation.Enable_HikariDataSourceMonitoring_At_Main;
import com.sptek._frameworkWebCore._annotation.annotationCondition.HasAnnotationOnMain_At_Bean;
import com.sptek._frameworkWebCore.base.constant.MainClassAnnotationRegister;
import com.sptek._frameworkWebCore.base.exception.ExceptionHelper;
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

    public SchedulerForHikariDataSourceMonitoring(@Qualifier("schedulerExecutorForHikariDataSourceMonitoring") ThreadPoolTaskScheduler schedulerExecutorForHikariDataSourceMonitoring) {
        this.schedulerExecutorForHikariDataSourceMonitoring = schedulerExecutorForHikariDataSourceMonitoring;
    }

    @EventListener // 시작에 MainClassAnnotationRegister 가 필요 함으로 ContextRefreshedEvent 을 기다려 시작함
    public void listen(ContextRefreshedEvent contextRefreshedEvent) {
        if (scheduledFuture != null) return;
        int SCHEDULE_WITH_FIXED_DELAY_SECONDS = 10;
        hikariDataSources = contextRefreshedEvent.getApplicationContext().getBeansOfType(HikariDataSource.class);
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
                    PoolName : %s
                    conf MaximumPoolSize : %s
                    conf MinimumIdle : %s
                    conf ConnectionTimeout : %s
                    conf IdleTimeout : %s
                    conf MaxLifetime : %s
                    conf ValidationTimeout : %s
                    
                    real TotalConnections : %s
                    real IdleConnections : %s
                    real ActiveConnections : %s
                    real ThreadsAwaitingConnection : %s
                    """
                    .formatted(
                            hikariDataSource.getPoolName()
                            , ExceptionHelper.exSafe(hikariConfigMXBean::getMaximumPoolSize, -1)
                            , ExceptionHelper.exSafe(hikariConfigMXBean::getMinimumIdle, -1)
                            , ExceptionHelper.exSafe(hikariConfigMXBean::getConnectionTimeout, -1)
                            , ExceptionHelper.exSafe(hikariConfigMXBean::getIdleTimeout, -1)
                            , ExceptionHelper.exSafe(hikariConfigMXBean::getMaxLifetime, -1)
                            , ExceptionHelper.exSafe(hikariConfigMXBean::getValidationTimeout, -1)


                            , ExceptionHelper.exSafe(hikariPoolMXBean::getTotalConnections, -1)
                            , ExceptionHelper.exSafe(hikariPoolMXBean::getIdleConnections, -1)
                            , ExceptionHelper.exSafe(hikariPoolMXBean::getActiveConnections, -1)
                            , ExceptionHelper.exSafe(hikariPoolMXBean::getThreadsAwaitingConnection, -1));

            String logTag = Objects.toString(MainClassAnnotationRegister.getAnnotationAttributes(Enable_HikariDataSourceMonitoring_At_Main.class).get("value"), "");
            log.info(LoggingUtil.makeFwLogForm("HikariDataSource Monitoring (Scheduler)", logContent, logTag));
        }
    }
}
