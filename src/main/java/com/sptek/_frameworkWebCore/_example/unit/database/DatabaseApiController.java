package com.sptek._frameworkWebCore._example.unit.database;

import com.sptek._frameworkWebCore._example.dto.TbTestDto;
import com.sptek._frameworkWebCore._annotation.Enable_ResponseOfApiCommonSuccess_At_RestController;
import com.sptek._frameworkWebCore._annotation.Enable_ResponseOfApiGlobalException_At_RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@Enable_ResponseOfApiCommonSuccess_At_RestController
@Enable_ResponseOfApiGlobalException_At_RestController
@RequestMapping(value = {"/api/"}, produces = {MediaType.APPLICATION_JSON_VALUE/*, MediaType.APPLICATION_XML_VALUE*/})
@Tag(name = "Database", description = "")

public class DatabaseApiController {
    private final DatabaseService databaseService;

    @GetMapping("/01/example/databasea/checkDbConnection")
    @Operation(summary = "01. DB 연결 상태 체크", description = "")
    public Object checkDbConnection() {
        return databaseService.checkDbConnection() == 1 ? "success" : "fail";
    }

    @GetMapping("/02/example/databasea/checkReplicationMaster")
    @Operation(summary = "02. @Transactional(readOnly = false) 통해 Master DB로 연결 체크", description = "")
    public Object checkReplicationMaster(Model model) {
        return databaseService.checkReplicationMaster() == 1 ? "success" : "fail";
    }

    @GetMapping("/03/example/databasea/checkReplicationSlave")
    @Operation(summary = "03. @Transactional(readOnly = true) 통해 Slave DB로 연결 체크", description = "")
    public Object checkReplicationSlave(Model model) {
        return databaseService.checkReplicationSlave() == 1 ? "success" : "fail";
    }

    @GetMapping("/04/example/databasea/myBatisCommonDaoInsert")
    @Operation(summary = "04. myBatisCommonDao insert", description = "")
    public Object myBatisCommonDaoInsert() {
        TbTestDto tbTestDto = TbTestDto.builder()
                .c1((int) (System.currentTimeMillis() % Integer.MAX_VALUE))
                .c2((int) (System.currentTimeMillis() % Integer.MAX_VALUE))
                .c3((int) (System.currentTimeMillis() % Integer.MAX_VALUE)).build();
        return databaseService.insertTbTest(tbTestDto) == 1 ? "success" : "fail";
    }

    @GetMapping("/05/example/databasea/myBatisCommonDaoUpdate")
    @Operation(summary = "05. myBatisCommonDao update", description = "")
    public Object myBatisCommonDaoUpdate() {
        TbTestDto tbTestDto = TbTestDto.builder()
                .c1((int) (System.currentTimeMillis() % Integer.MAX_VALUE))
                .c2((int) (System.currentTimeMillis() % Integer.MAX_VALUE))
                .c3((int) (System.currentTimeMillis() % Integer.MAX_VALUE)).build();
        return databaseService.updateTbTest(tbTestDto) > 0 ? "success" : "fail";
    }

    @GetMapping("/06/example/databasea/myBatisCommonDaoDelete")
    @Operation(summary = "06. myBatisCommonDao delete", description = "")
    public Object myBatisCommonDaoDelete() {
        return databaseService.deleteTbTest() > 0 ? "success" : "fail";
    }

    @GetMapping("/07/example/databasea/myBatisCommonDaoSelectOne")
    @Operation(summary = "07. myBatisCommonDao selectOne", description = "")
    public Object myBatisCommonDaoSelectOne() {
        return databaseService.getOneTbTest();
    }

    @GetMapping("/08/example/databasea/myBatisCommonDaoSelectList")
    @Operation(summary = "08. myBatisCommonDao selectList", description = "")
    public Object myBatisCommonDaoSelectList() {
        return databaseService.getListTbTest();
    }
    
    @GetMapping("/09/example/databasea/myBatisCommonDaoSelectListWithResultHandler")
    @Operation(summary = "09. myBatisCommonDao selectListWithResultHandler", description = "")
    public Object myBatisCommonDaoSelectListWithResultHandler() {
        return databaseService.getListTbTestWithResultHandler();
    }

    @GetMapping("/10/example/databasea/myBatisCommonDaoSelectMap")
    @Operation(summary = "10. myBatisCommonDao selectMap(단일, 리스트)", description = "")
    public Object myBatisCommonDaoSelectMap() {
        return databaseService.getMapTbTest();
    }

    @GetMapping("/11/example/databasea/myBatisCommonDaoSelectListWithPagination")
    @Operation(summary = "11. myBatisCommonDao selectListWithPagination", description = "")
    public Object myBatisCommonDaoSelectListWithPagination(
            @RequestParam(name = "currentPageNum", required = false, defaultValue = "1") int currentPageNum,
            @RequestParam(name = "setRowSizePerPage", required = false, defaultValue = "20") int setRowSizePerPage,
            @RequestParam(name = "setBottomPageNavigationSize", required = false, defaultValue = "10") int setBottomPageNavigationSize) {
        return databaseService.getListTbTestWithPagination();
    }
}
