package com.sptek._frameworkWebCore.schedule.scheduler;

import com.sptek._frameworkWebCore.util.SptFwUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.pool.PoolStats;
import org.apache.hc.core5.util.TimeValue;
import org.apache.ibatis.datasource.pooled.PoolState;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class PoolingHttpClientConnectionManagerMonitoringScheduler {
    @Qualifier("ForPoolingHttpClientConnectionManagerMonitoring")
    private final TaskScheduler ForPoolingHttpClientConnectionManagerMonitoring;
    private final PoolingHttpClientConnectionManager connectionManager;

    @PostConstruct
    public void init() {
        // 작업1
        ForPoolingHttpClientConnectionManagerMonitoring.scheduleWithFixedDelay(() -> {
            try {
                // --- 상태 캡처: 정리 전 ---
                PoolStats beforeStats = connectionManager.getTotalStats();
                String beforeStatsStr = String.format("[Before -> After]\n"
                        + "Leased:%d, "
                        + "Available:%d, "
                        + "Pending:%d"
                        , beforeStats.getLeased()
                        , beforeStats.getAvailable()
                        , beforeStats.getPending());
                // --- 커넥션 정리 수행 ---
                connectionManager.closeIdle(TimeValue.ofSeconds(10));
                connectionManager.closeExpired();
                // --- 상태 캡처: 정리 후 ---
                PoolStats afterStats = connectionManager.getTotalStats();
                String afterStatsStr = String.format("\n"
                                + "Leased:%d, "
                                + "Available:%d, "
                                + "Pending:%d\n"
                        , afterStats.getLeased()
                        , afterStats.getAvailable()
                        , afterStats.getPending());
                log.info(SptFwUtil.convertSystemNotice(this.getClass().getSimpleName(), "PoolingHttpClientConnectionManagerMonitoring", beforeStatsStr + afterStatsStr));

            } catch (Exception e) {
                log.warn("Error while monitoring HttpClient Connection Pool", e);
            }
        }, Duration.ofSeconds(6));

        // 작업2, 3..
    }
}
