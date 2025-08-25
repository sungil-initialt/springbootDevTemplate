package com.sptek._frameworkWebCore._example.unit.async;

import com.sptek._frameworkWebCore._example.dto.ExUserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service

public class AsyncService {

    public void justSleep5s() throws Exception {
        Thread.sleep(6_000L);
        log.info("justSleep5s done.");
    }

    @Async
    public void justSleep5sWithAsync() throws Exception {
        Thread.sleep(6_000L);
        log.info("justSleep5sWithAsync done.");
    }

    public ExUserDto getUser() throws Exception {
        Thread.sleep(6_000L);
        return ExUserDto.builder().id("sungilry").name("이성일").type(ExUserDto.UserType.manager).build();
    }

    public CompletableFuture<ExUserDto> getCompletableFutureUser() throws Exception {
        Thread.sleep(6_000L);
        ExUserDto exUserDto = ExUserDto.builder().id("sungilry").name("이성일").type(ExUserDto.UserType.manager).build();
        return CompletableFuture.completedFuture(exUserDto);
    }

    @Async
    public CompletableFuture<ExUserDto> getUserAsync() throws Exception {
        Thread.sleep(6_000L);
        ExUserDto exUserDto = ExUserDto.builder().id("sungilry").name("이성일").type(ExUserDto.UserType.manager).build();
        return CompletableFuture.completedFuture(exUserDto);
    }
}
