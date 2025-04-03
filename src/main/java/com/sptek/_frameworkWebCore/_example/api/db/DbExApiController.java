package com.sptek._frameworkWebCore._example.api.db;

import com.sptek._frameworkWebCore._example.dto.Tb_TestDto;
import com.sptek._frameworkWebCore._example.dto.Tb_ZipcodeDto;
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

import java.util.List;
import java.util.Map;

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

    @GetMapping("/insert")
    @Operation(summary = "Tb_Test insert (시스템 시간(ms))", description = "")
    public Object insert() {
        Tb_TestDto tbTestDto = Tb_TestDto.builder()
                .c1((int) (System.currentTimeMillis() % Integer.MAX_VALUE))
                .c2((int) (System.currentTimeMillis() % Integer.MAX_VALUE))
                .c3((int) (System.currentTimeMillis() % Integer.MAX_VALUE)).build();

        return dbExService.insertTB_Test(tbTestDto) == 1 ? "success" : "fail";
    }

    @GetMapping("/update")
    @Operation(summary = "Tb_Test update (시스템 시간(ms))", description = "")
    public Object update() {
        Tb_TestDto tbTestDto = Tb_TestDto.builder()
                .c1((int) (System.currentTimeMillis() % Integer.MAX_VALUE))
                .c2((int) (System.currentTimeMillis() % Integer.MAX_VALUE))
                .c3((int) (System.currentTimeMillis() % Integer.MAX_VALUE)).build();

        return dbExService.updateTb_Test(tbTestDto) == 1 ? "success" : "fail";
    }

    @GetMapping("/delete")
    @Operation(summary = "Tb_Test delete (최신값)", description = "")
    public Object delete() {
        return dbExService.deleteTb_Test() == 1 ? "success" : "fail";
    }
    
    @GetMapping("/selectOne")
    @Operation(summary = "Tb_Test select (단일)", description = "")
    public Object selectOne() {
        return dbExService.getOneTB_Test();
    }

    @GetMapping("/selectList")
    @Operation(summary = "Tb_Test select (리스트)", description = "")
    public Object selectList() {
        return dbExService.getListTB_Test();
    }

    
    
    
    @GetMapping("/selectListWithResultHandler")
    @Operation(summary = "ResultHandler를 사용해 Tb_Test select (리스트)", description = "")
    public List<Tb_ZipcodeDto> selectListWithResultHandler() {
        return dbExService.selectListWithResultHandler();
    }

    @GetMapping("/selectMap")
    @Operation(summary = "Tb_Test select (단일, 리스트)", description = "")
    public Map<?, ?> selectMap() {
        return dbExService.selectMap();
    }

    @GetMapping("/selectPaginate")
    @Operation(summary = "페이징된 Tb_Test select (리스트)", description = "")
    public PageInfoSupport<Tb_ZipcodeDto> selectPaginate(
            @RequestParam(name = "currentPageNum", required = false, defaultValue = "0") int currentPageNum,
            @RequestParam(name = "setRowSizePerPage", required = false, defaultValue = "0") int setRowSizePerPage,
            @RequestParam(name = "setButtomPageNavigationSize", required = false, defaultValue = "0") int setButtomPageNavigationSize) {

        return dbExService.selectPaginate(currentPageNum, setRowSizePerPage, setButtomPageNavigationSize);
    }


}
