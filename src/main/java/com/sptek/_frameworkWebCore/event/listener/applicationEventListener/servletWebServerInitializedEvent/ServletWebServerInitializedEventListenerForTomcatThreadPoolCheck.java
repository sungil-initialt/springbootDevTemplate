package com.sptek._frameworkWebCore.event.listener.applicationEventListener.servletWebServerInitializedEvent;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.ProtocolHandler;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ServletWebServerInitializedEventListenerForTomcatThreadPoolCheck {
    private volatile TomcatWebServer tomcatWebServer;

    // todo: event Listener 는 event 를 발생시킨 Thread에 종속되어 처리된다. 
    //  listener 처리가 길어지면 해당 event를 발생시킨 Thread가 계속 홀딩되어 지연 됨으로 작업이 길경우 @Async 등을 통해 Thread 분리가 필요하다.
    //  system 에서 발생시키는 event도 동일하게 하나의 Thread 에서 여러 linstenr 로 전달 되기 때문에 하나의 listener 가 작업이 끝나며 다른 listner 의 작업이 가능함
    @Async 
    @EventListener 
    public void onWebServerReady(ServletWebServerInitializedEvent servletWebServerInitializedEvent) throws InterruptedException {
        log.debug("Event!");
        if (servletWebServerInitializedEvent.getWebServer() instanceof TomcatWebServer tomcatWebServer) {
            this.tomcatWebServer = tomcatWebServer;
            logThreadPoolInfo();
        }
    }

    public void logThreadPoolInfo() throws InterruptedException {
        if (tomcatWebServer == null) {
            log.warn("TomcatWebServer not initialized yet");
            return;
        }
        while(true) {
            Thread.sleep(1000);
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
        }
    }
}
