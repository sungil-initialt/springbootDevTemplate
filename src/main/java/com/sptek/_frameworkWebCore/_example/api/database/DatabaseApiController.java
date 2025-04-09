package com.sptek._frameworkWebCore._example.api.database;

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
@RequestMapping(value = {"/api/v1/example/database/"}, produces = {MediaType.APPLICATION_JSON_VALUE/*, MediaType.APPLICATION_XML_VALUE*/})
@Tag(name = "database", description = "")

public class DatabaseApiController {
    private final DatabaseService databaseService;

    @GetMapping("/checkDbConnection")
    @Operation(summary = "1. DB 연결 상태 체크", description = "")
    public Object checkDbConnection() {
        return databaseService.checkDbConnection() == 1 ? "success" : "fail";
    }

    @GetMapping("/checkReplicationMaster")
    @Operation(summary = "2. @Transactional(readOnly = false) 통해 Master DB로 연결 체크", description = "")
    public Object checkReplicationMaster(Model model) {
        return databaseService.checkReplicationMaster() == 1 ? "success" : "fail";
    }

    @GetMapping("/checkReplicationSlave")
    @Operation(summary = "3. @Transactional(readOnly = true) 통해 Slave DB로 연결 체크", description = "")
    public Object checkReplicationSlave(Model model) {
        return databaseService.checkReplicationSlave() == 1 ? "success" : "fail";
    }

    @GetMapping("/myBatisCommonDaoInsert")
    @Operation(summary = "4. myBatisCommonDao insert", description = "")
    public Object myBatisCommonDaoInsert() {
        TbTestDto tbTestDto = TbTestDto.builder()
                .c1((int) (System.currentTimeMillis() % Integer.MAX_VALUE))
                .c2((int) (System.currentTimeMillis() % Integer.MAX_VALUE))
                .c3((int) (System.currentTimeMillis() % Integer.MAX_VALUE)).build();
        return databaseService.insertTbTest(tbTestDto) == 1 ? "success" : "fail";
    }

    @GetMapping("/myBatisCommonDaoUpdate")
    @Operation(summary = "5. myBatisCommonDao update", description = "")
    public Object myBatisCommonDaoUpdate() {
        TbTestDto tbTestDto = TbTestDto.builder()
                .c1((int) (System.currentTimeMillis() % Integer.MAX_VALUE))
                .c2((int) (System.currentTimeMillis() % Integer.MAX_VALUE))
                .c3((int) (System.currentTimeMillis() % Integer.MAX_VALUE)).build();
        return databaseService.updateTbTest(tbTestDto) == 1 ? "success" : "fail";
    }

    @GetMapping("/myBatisCommonDaoDelete")
    @Operation(summary = "6. myBatisCommonDao delete", description = "")
    public Object myBatisCommonDaoDelete() {
        return databaseService.deleteTbTest() == 1 ? "success" : "fail";
    }

    @GetMapping("/myBatisCommonDaoSelectOne")
    @Operation(summary = "7. myBatisCommonDao selectOne", description = "")
    public Object myBatisCommonDaoSelectOne() {
        return databaseService.getOneTbTest();
    }

    @GetMapping("/myBatisCommonDaoSelectList")
    @Operation(summary = "8. myBatisCommonDao selectList", description = "")
    public Object myBatisCommonDaoSelectList() {
        return databaseService.getListTbTest();
    }
    
    @GetMapping("/myBatisCommonDaoSelectListWithResultHandler")
    @Operation(summary = "9. myBatisCommonDao selectListWithResultHandler", description = "")
    public Object myBatisCommonDaoSelectListWithResultHandler() {
        return databaseService.getListTbTestWithResultHandler();
    }

    @GetMapping("/myBatisCommonDaoSelectMap")
    @Operation(summary = "10. myBatisCommonDao selectMap(단일, 리스트)", description = "")
    public Object myBatisCommonDaoSelectMap() {
        return databaseService.getMapTbTest();
    }

    @GetMapping("/myBatisCommonDaoSelectListWithPagination")
    @Operation(summary = "11. myBatisCommonDao selectListWithPagination", description = "")
    public Object myBatisCommonDaoSelectListWithPagination(
            @RequestParam(name = "currentPageNum", required = false, defaultValue = "1") int currentPageNum,
            @RequestParam(name = "setRowSizePerPage", required = false, defaultValue = "20") int setRowSizePerPage,
            @RequestParam(name = "setBottomPageNavigationSize", required = false, defaultValue = "10") int setBottomPageNavigationSize) {
        return databaseService.getListTbTestWithPagination();
    }
}
