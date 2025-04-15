package com.sptek._frameworkWebCore._example.unit.database;

import com.sptek._frameworkWebCore._example.dto.TbTestDto;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@EnableResponseOfApiCommonSuccess_InRestController
@EnableResponseOfApiGlobalException_InRestController
@RequestMapping(value = {"/api/v1/example/"}, produces = {MediaType.APPLICATION_JSON_VALUE/*, MediaType.APPLICATION_XML_VALUE*/})
@Tag(name = "database", description = "")

public class DatabaseApiController {
    private final DatabaseService databaseService;

    @GetMapping("/public/databasea/a_checkDbConnection")
    @Operation(summary = "DB 연결 상태 체크", description = "")
    public Object checkDbConnection() {
        return databaseService.checkDbConnection() == 1 ? "success" : "fail";
    }

    @GetMapping("/public/databasea/b_checkReplicationMaster")
    @Operation(summary = "@Transactional(readOnly = false) 통해 Master DB로 연결 체크", description = "")
    public Object checkReplicationMaster(Model model) {
        return databaseService.checkReplicationMaster() == 1 ? "success" : "fail";
    }

    @GetMapping("/public/databasea/c_checkReplicationSlave")
    @Operation(summary = "@Transactional(readOnly = true) 통해 Slave DB로 연결 체크", description = "")
    public Object checkReplicationSlave(Model model) {
        return databaseService.checkReplicationSlave() == 1 ? "success" : "fail";
    }

    @GetMapping("/public/databasea/d_myBatisCommonDaoInsert")
    @Operation(summary = "myBatisCommonDao insert", description = "")
    public Object myBatisCommonDaoInsert() {
        TbTestDto tbTestDto = TbTestDto.builder()
                .c1((int) (System.currentTimeMillis() % Integer.MAX_VALUE))
                .c2((int) (System.currentTimeMillis() % Integer.MAX_VALUE))
                .c3((int) (System.currentTimeMillis() % Integer.MAX_VALUE)).build();
        return databaseService.insertTbTest(tbTestDto) == 1 ? "success" : "fail";
    }

    @GetMapping("/public/databasea/e_myBatisCommonDaoUpdate")
    @Operation(summary = "myBatisCommonDao update", description = "")
    public Object myBatisCommonDaoUpdate() {
        TbTestDto tbTestDto = TbTestDto.builder()
                .c1((int) (System.currentTimeMillis() % Integer.MAX_VALUE))
                .c2((int) (System.currentTimeMillis() % Integer.MAX_VALUE))
                .c3((int) (System.currentTimeMillis() % Integer.MAX_VALUE)).build();
        return databaseService.updateTbTest(tbTestDto) == 1 ? "success" : "fail";
    }

    @GetMapping("/public/databasea/f_myBatisCommonDaoDelete")
    @Operation(summary = "myBatisCommonDao delete", description = "")
    public Object myBatisCommonDaoDelete() {
        return databaseService.deleteTbTest() == 1 ? "success" : "fail";
    }

    @GetMapping("/public/databasea/g_myBatisCommonDaoSelectOne")
    @Operation(summary = "myBatisCommonDao selectOne", description = "")
    public Object myBatisCommonDaoSelectOne() {
        return databaseService.getOneTbTest();
    }

    @GetMapping("/public/databasea/h_myBatisCommonDaoSelectList")
    @Operation(summary = "myBatisCommonDao selectList", description = "")
    public Object myBatisCommonDaoSelectList() {
        return databaseService.getListTbTest();
    }
    
    @GetMapping("/public/databasea/i_myBatisCommonDaoSelectListWithResultHandler")
    @Operation(summary = "myBatisCommonDao selectListWithResultHandler", description = "")
    public Object myBatisCommonDaoSelectListWithResultHandler() {
        return databaseService.getListTbTestWithResultHandler();
    }

    @GetMapping("/public/databasea/j_myBatisCommonDaoSelectMap")
    @Operation(summary = "myBatisCommonDao selectMap(단일, 리스트)", description = "")
    public Object myBatisCommonDaoSelectMap() {
        return databaseService.getMapTbTest();
    }

    @GetMapping("/public/databasea/k_myBatisCommonDaoSelectListWithPagination")
    @Operation(summary = "myBatisCommonDao selectListWithPagination", description = "")
    public Object myBatisCommonDaoSelectListWithPagination(
            @RequestParam(name = "currentPageNum", required = false, defaultValue = "1") int currentPageNum,
            @RequestParam(name = "setRowSizePerPage", required = false, defaultValue = "20") int setRowSizePerPage,
            @RequestParam(name = "setBottomPageNavigationSize", required = false, defaultValue = "10") int setBottomPageNavigationSize) {
        return databaseService.getListTbTestWithPagination();
    }
}
