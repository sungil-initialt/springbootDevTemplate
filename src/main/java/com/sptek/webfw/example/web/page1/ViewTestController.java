package com.sptek.webfw.example.web.page1;

import com.sptek.webfw.anotation.AnoInterceptorCheck;
import com.sptek.webfw.example.dto.TBTestDto;
import com.sptek.webfw.example.dto.TBZipcodeDto;
import com.sptek.webfw.support.CommonControllerSupport;
import com.sptek.webfw.support.PageInfoSupport;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping(value = "", produces = MediaType.TEXT_HTML_VALUE)
public class ViewTestController extends CommonControllerSupport {
    private final String PAGE_BASE_PATH = "pages/example/page1/";
    @Autowired
    private ViewTestService viewTestService;

    //기본 테스트
    @RequestMapping({"/", "/welcome"})
    public String welcome(Model model) {
        log.debug("called welcome");
        model.addAttribute("message", "welcome");

        return PAGE_BASE_PATH + "welcome";
    }

    @RequestMapping("/interceptorTest")
    @AnoInterceptorCheck
    public String interceptorTest(Model model) {
        log.debug("called interceptorTest : first log");
        model.addAttribute("message", "welcome");

        log.debug("called interceptorTest : just before return");
        return PAGE_BASE_PATH + "interceptorTest";
    }

    //내부 로직에직 발생한 EX에 대한 처리.
    @RequestMapping("/serviceErr")
    public String serviceErr(Model model) {
        log.debug("called serviceErr");
        
        if(1==1) throw new NullPointerException("NP Exception for Test");
        //if(1==1) throw new ApiServiceException(ApiErrorCode.SERVICE_DEFAULT_ERROR, "serviceErr");
        
        return PAGE_BASE_PATH + "xx"; //위에서 에러가 발생함으로 뷰로 갈일은 없음";
    }

    //Mybatis 를 통한 DB 테스트들
    @RequestMapping("/dbConnectTest")
    public String dbConnectTest(Model model) {
        log.debug("called dbConnectTest");
        int result = viewTestService.returnOne();
        model.addAttribute("result", result);
        return PAGE_BASE_PATH + "simpleModelView";
    }

    @RequestMapping("/replicationMasterTest")
    public String replicationMasterTest(Model model) {
        log.debug("called replicationMasterTest");
        int result = viewTestService.replicationMasterTest();
        model.addAttribute("result", result);
        return PAGE_BASE_PATH + "simpleModelView";
    }

    @RequestMapping("/replicationSlaveTest")
    public String replicationSlaveTest(Model model) {
        log.debug("called replicationSlaveTest");
        int result = viewTestService.replicationSlaveTest();
        model.addAttribute("result", result);
        return PAGE_BASE_PATH + "simpleModelView";
    }

    @RequestMapping("/selectOneTest")
    public String selectOneTest(Model model) {
        log.debug("called selectOneTest");
        TBTestDto tbTestDto = viewTestService.selectOneTest();
        model.addAttribute("result", tbTestDto.toString());
        return PAGE_BASE_PATH + "simpleModelView";
    }

    @RequestMapping("/selectListTest")
    public String selectListTest(Model model) {
        log.debug("called selectListTest");
        List<TBTestDto> tbTestDtos = viewTestService.selectListTest();
        model.addAttribute("result", tbTestDtos.toString());
        return PAGE_BASE_PATH + "simpleModelView";
    }

    @RequestMapping("/selectListWithResultHandlerTest")
    //ResultHandler를 이용해서 db에서 result row를 하나씩 읽어와 각 row에 대한 처리가 가능함
    public String selectListWithResultHandlerTest(Model model) {
        log.debug("called selectListWithResultHandlerTest");
        List<TBZipcodeDto> tBZipcode = viewTestService.selectListWithResultHandlerTest();
        model.addAttribute("result", tBZipcode.toString());
        return PAGE_BASE_PATH + "simpleModelView";
    }

    @RequestMapping("/selectMapTest")
    //result 결과 list를 map 형태로 받아올수 있다.
    public String selectMapTest(Model model) {
        log.debug("called selectMapTest");
        Map<?, ?> resultMap = viewTestService.selectMapTest();
        model.addAttribute("result", resultMap.toString());
        return PAGE_BASE_PATH + "simpleModelView";
    }

    @RequestMapping("/selectPaginateTest")
    //PageHelperSupport를 이용해서 페이지 처리가 가능한 형태로 result row를 읽어 온다.
    public String selectPaginateTest(HttpServletRequest request,
                                     @RequestParam(name = "currentPageNum", required = false, defaultValue = "0") int currentPageNum,
                                     @RequestParam(name = "setRowSizePerPage", required = false, defaultValue = "0") int setRowSizePerPage,
                                     @RequestParam(name = "setButtomPageNavigationSize", required = false, defaultValue = "0") int setButtomPageNavigationSize,
                                     Model model) {
        //값이 0일때는 디폴트값으로 적용됨
        log.debug("called selectPaginateTest");

        PageInfoSupport<TBZipcodeDto> pageInfoSupport = viewTestService.selectPaginateTest(currentPageNum, setRowSizePerPage, setButtomPageNavigationSize);

        model.addAttribute("result", pageInfoSupport.toString());
        return PAGE_BASE_PATH + "simpleModelView";
    }

    @RequestMapping("/insertTest")
    public String insertTest(Model model) {
        log.debug("called insertTest");
        TBTestDto tbTestDto = TBTestDto.builder()
                .c1(41)
                .c2(42)
                .c3(43).build();

        int result = viewTestService.insertTest(tbTestDto);
        model.addAttribute("result", result);
        return PAGE_BASE_PATH + "simpleModelView";
    }

    @RequestMapping("/updateTest")
    public String updateTest(Model model) {
        log.debug("called updateTest");
        TBTestDto tbTestDto = TBTestDto.builder()
                .c1(41)
                .c2(422)
                .c3(433).build();

        int result = viewTestService.updateTest(tbTestDto);
        model.addAttribute("result", result);
        return PAGE_BASE_PATH + "simpleModelView";
    }

    @RequestMapping("/deleteTest")
    public String deleteTest(Model model) {
        log.debug("called deleteTest");
        TBTestDto tbTestDto = TBTestDto.builder()
                .c1(41).build();

        int result = viewTestService.deleteTest(tbTestDto);
        model.addAttribute("result", result);
        return PAGE_BASE_PATH + "simpleModelView";
    }

    @RequestMapping("/i18nTest")
    public String i18nTest(Model model) {
        log.debug("called i18nTest");

        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        String connectTime = getI18nMessage("connectTime", new String[]{formattedDateTime});
        model.addAttribute("connectTime", connectTime);
        return PAGE_BASE_PATH + "i18n";
    }
}
