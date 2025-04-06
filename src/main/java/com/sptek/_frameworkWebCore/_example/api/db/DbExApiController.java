package com.sptek._frameworkWebCore._example.api.db;

import com.sptek._frameworkWebCore._example.dto.TbTestDto;
import com.sptek._frameworkWebCore._example.dto.TbZipcodeDto;
import com.sptek._frameworkWebCore.annotation.EnableResponseOfApiCommonSuccess_InRestController;
import com.sptek._frameworkWebCore.annotation.EnableResponseOfApiGlobalException_InRestController;
import com.sptek._frameworkWebCore.support.PageInfoSupport;
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
@RequestMapping(value = {"/api/v1/example/"}, produces = {MediaType.APPLICATION_JSON_VALUE/*, MediaType.APPLICATION_XML_VALUE*/}) // 클라이언트가 Accept 해더를 보낼 경우 제공하는 미디어 타입이 일치해야함(없으면 406)
@Tag(name = "DB 관련 예시", description = "")

public class DbExApiController {
    private final DbExService dbExService;

    @GetMapping("/checkDbConnection")
    @Operation(summary = "쿼리 실행을 통해 DB 연결 상태 체크", description = "")
    public Object checkDbConnection() {
        return dbExService.checkDbConnection() == 1 ? "success" : "fail";
    }

    @GetMapping("/checkReplicationMaster")
    @Operation(summary = "@Transactional(readOnly = false) 통해 Master DB로 연결", description = "")
    public Object checkReplicationMaster(Model model) {
        return dbExService.checkReplicationMaster() == 1 ? "success" : "fail";
    }

    @GetMapping("/checkReplicationSlave")
    @Operation(summary = "@Transactional(readOnly = true) 통해 Slave DB로 연결", description = "")
    public Object checkReplicationSlave(Model model) {
        return dbExService.checkReplicationSlave() == 1 ? "success" : "fail";
    }

    @GetMapping("/insertTbTest")
    @Operation(summary = "Tb_Test insert (시스템 시간(ms))", description = "")
    public Object insertTbTest() {
        TbTestDto tbTestDto = TbTestDto.builder()
                .c1((int) (System.currentTimeMillis() % Integer.MAX_VALUE))
                .c2((int) (System.currentTimeMillis() % Integer.MAX_VALUE))
                .c3((int) (System.currentTimeMillis() % Integer.MAX_VALUE)).build();
        return dbExService.insertTbTest(tbTestDto) == 1 ? "success" : "fail";
    }

    @GetMapping("/updateTbTest")
    @Operation(summary = "Tb_Test update (시스템 시간(ms))", description = "")
    public Object updateTbTest() {
        TbTestDto tbTestDto = TbTestDto.builder()
                .c1((int) (System.currentTimeMillis() % Integer.MAX_VALUE))
                .c2((int) (System.currentTimeMillis() % Integer.MAX_VALUE))
                .c3((int) (System.currentTimeMillis() % Integer.MAX_VALUE)).build();
        return dbExService.updateTbTest(tbTestDto) == 1 ? "success" : "fail";
    }

    @GetMapping("/deleteTbTest")
    @Operation(summary = "Tb_Test delete (최신값)", description = "")
    public Object deleteTbTest() {
        return dbExService.deleteTbTest() == 1 ? "success" : "fail";
    }

    @GetMapping("/getOneTbTest")
    @Operation(summary = "Tb_Test select (단일)", description = "")
    public Object getOneTbTest() {
        return dbExService.getOneTbTest();
    }

    @GetMapping("/getListTbTest")
    @Operation(summary = "Tb_Test select (리스트)", description = "")
    public Object getListTbTest() {
        return dbExService.getListTbTest();
    }
    
    @GetMapping("/getListTbTestWithResultHandler")
    @Operation(summary = "ResultHandler를 사용해 Tb_Test select (리스트)", description = "")
    public Object getListTbTestWithResultHandler() {
        return dbExService.getListTbTestWithResultHandler();
    }

    @GetMapping("/getMapTbTest")
    @Operation(summary = "Tb_Test select (단일, 리스트)", description = "")
    public Object getMapTbTest() {
        return dbExService.getMapTbTest();
    }

    @GetMapping("/getListTbTestWithPagination")
    @Operation(summary = "Paging 된 Tb_Test select (리스트)", description = "")
    public Object selectPaginate(
            @RequestParam(name = "currentPageNum", required = false, defaultValue = "0") int currentPageNum,
            @RequestParam(name = "setRowSizePerPage", required = false, defaultValue = "0") int setRowSizePerPage,
            @RequestParam(name = "setBottomPageNavigationSize", required = false, defaultValue = "0") int setBottomPageNavigationSize) {
        return dbExService.getListTbTestWithPagination();
    }
}
