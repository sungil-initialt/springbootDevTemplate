package com.sptek._frameworkWebCore._example.api.db;

import com.sptek._frameworkWebCore.annotation.EnableResponseOfApiCommonSuccess_InRestController;
import com.sptek._frameworkWebCore.annotation.EnableResponseOfApiGlobalException_InRestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@EnableResponseOfApiCommonSuccess_InRestController
@EnableResponseOfApiGlobalException_InRestController
@RequestMapping(value = {"/api/v1/", /*"/api/v2/"*/}, produces = {MediaType.APPLICATION_JSON_VALUE/*, MediaType.APPLICATION_XML_VALUE*/}) // 클라이언트가 Accept 해더를 보낼 경우 제공하는 미디어 타입이 일치해야함(없으면 406)
@Tag(name = "DB 관련 예시", description = "")

public class DbExApiController {
    private final DbExService dbExService;

    @GetMapping("/checkDbConnection")
    @Operation(summary = "쿼리 실행을 통해 DB 연결 상태 체크", description = "", tags = {""})
    public Object checkDbConnection() {
        return dbExService.checkDbConnection() == 1 ? "DB Connection Successful" : "DB Connection Failed";
    }

    @GetMapping("/checkReplicationMaster")
    @Operation(summary = "@Transactional(readOnly = false) 통해 Master DB로 연결", description = "", tags = {""})
    public Object checkReplicationMaster(Model model) {
        return dbExService.checkReplicationMaster() == 1 ? "DB Master Connection Successful" : "DB Master Failed";
    }

    @GetMapping("/checkReplicationSlave")
    @Operation(summary = "@Transactional(readOnly = true) 통해 Slave DB로 연결", description = "", tags = {""})
    public Object checkReplicationSlave(Model model) {
        return dbExService.checkReplicationSlave() == 1 ? "DB Slave Connection Successful" : "DB Slave Failed";
    }
}
