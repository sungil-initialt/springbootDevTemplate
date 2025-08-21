package com.sptek._frameworkWebCore._example.unit.async;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service

public class AsyncService {

    public void justSleep5s() throws Exception {
        Thread.sleep(5_000L);
        log.info("justSleep5s done.");
    }

    @Async
    public void justSleep5sWithAsync() throws Exception {
        Thread.sleep(5_000L);
        log.info("justSleep5sWithAsync done.");
    }

}
