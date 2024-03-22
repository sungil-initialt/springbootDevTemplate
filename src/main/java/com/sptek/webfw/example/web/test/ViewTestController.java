package com.sptek.webfw.example.web.test;

import com.sptek.webfw.anotation.AnoInterceptorCheck;
import com.sptek.webfw.anotation.AnoRequestDeduplication;
import com.sptek.webfw.code.ErrorCode;
import com.sptek.webfw.example.dto.TBTestDto;
import com.sptek.webfw.example.dto.TBZipcodeDto;
import com.sptek.webfw.example.dto.ValidationTestDto;
import com.sptek.webfw.exceptionHandler.exception.ServiceException;
import com.sptek.webfw.support.CommonControllerSupport;
import com.sptek.webfw.support.PageInfoSupport;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    private final String PAGE_BASE_PATH = "pages/example/test/";
    @Autowired
    private ViewTestService viewTestService;

    //기본 테스트
    //@RequestMapping({"/", "/welcome"})
    @RequestMapping({"/welcome"})
    public String welcome(Model model) {
        model.addAttribute("message", "welcome");
        return PAGE_BASE_PATH + "welcome";
    }

    @RequestMapping("/interceptor")
    @AnoInterceptorCheck
    public String interceptor(Model model) {
        log.debug("origin caller is called");
        model.addAttribute("message", "welcome");
        return PAGE_BASE_PATH + "interceptor";
    }

    //내부 로직에직 발생한 EX에 대한 처리.
    @RequestMapping("/serviceErr")
    public String serviceErr(Model model) {
        //if(1==1) throw new NullPointerException("NP Exception for Test");
        if(1==1) throw ServiceException.builder().errorCode(ErrorCode.SERVICE_DEFAULT_ERROR).build();

        return PAGE_BASE_PATH + "xx"; //there's no way to reach here
    }

    //Mybatis 를 통한 DB 테스트들
    @RequestMapping("/dbConnect")
    public String dbConnect(Model model) {
        int result = viewTestService.returnOne();
        model.addAttribute("result", result);
        return PAGE_BASE_PATH + "simpleModelView";
    }

    @RequestMapping("/replicationMaster")
    public String replicationMaster(Model model) {
        int result = viewTestService.replicationMaster();
        model.addAttribute("result", result);
        return PAGE_BASE_PATH + "simpleModelView";
    }

    @RequestMapping("/replicationSlave")
    public String replicationSlave(Model model) {
        int result = viewTestService.replicationSlave();
        model.addAttribute("result", result);
        return PAGE_BASE_PATH + "simpleModelView";
    }

    @RequestMapping("/selectOne")
    public String selectOne(Model model) {
        TBTestDto tbTestDto = viewTestService.selectOne();
        model.addAttribute("result", tbTestDto.toString());
        return PAGE_BASE_PATH + "simpleModelView";
    }

    @RequestMapping("/selectList")
    public String selectList(Model model) {
        List<TBTestDto> tbTestDtos = viewTestService.selectList();
        model.addAttribute("result", tbTestDtos.toString());
        return PAGE_BASE_PATH + "simpleModelView";
    }

    @RequestMapping("/selectListWithResultHandler")
    //ResultHandler를 이용해서 db에서 result row를 하나씩 읽어와 각 row에 대한 처리가 가능함
    public String selectListWithResultHandler(Model model) {
        List<TBZipcodeDto> tBZipcode = viewTestService.selectListWithResultHandler();
        model.addAttribute("result", tBZipcode.toString());
        return PAGE_BASE_PATH + "simpleModelView";
    }

    @RequestMapping("/selectMap")
    //result 결과 list를 map 형태로 받아올수 있다.
    public String selectMap(Model model) {
        Map<?, ?> resultMap = viewTestService.selectMap();
        model.addAttribute("result", resultMap.toString());
        return PAGE_BASE_PATH + "simpleModelView";
    }

    @RequestMapping("/selectPaginate")
    //PageHelperSupport를 이용해서 페이지 처리가 가능한 형태로 result row를 읽어 온다.
    public String selectPaginate(HttpServletRequest request,
                                     @RequestParam(name = "currentPageNum", required = false, defaultValue = "0") int currentPageNum,
                                     @RequestParam(name = "setRowSizePerPage", required = false, defaultValue = "0") int setRowSizePerPage,
                                     @RequestParam(name = "setButtomPageNavigationSize", required = false, defaultValue = "0") int setButtomPageNavigationSize,
                                     Model model) {

        PageInfoSupport<TBZipcodeDto> pageInfoSupport = viewTestService.selectPaginate(currentPageNum, setRowSizePerPage, setButtomPageNavigationSize);
        model.addAttribute("result", pageInfoSupport.toString());
        return PAGE_BASE_PATH + "simpleModelView";
    }

    @RequestMapping("/insert")
    public String insert(Model model) {
        TBTestDto tbTestDto = TBTestDto.builder()
                .c1(41)
                .c2(42)
                .c3(43).build();

        int result = viewTestService.insert(tbTestDto);
        model.addAttribute("result", result);
        return PAGE_BASE_PATH + "simpleModelView";
    }

    @RequestMapping("/update")
    public String update(Model model) {
        TBTestDto tbTestDto = TBTestDto.builder()
                .c1(41)
                .c2(422)
                .c3(433).build();

        int result = viewTestService.update(tbTestDto);
        model.addAttribute("result", result);
        return PAGE_BASE_PATH + "simpleModelView";
    }

    @RequestMapping("/delete")
    public String delete(Model model) {
        TBTestDto tbTestDto = TBTestDto.builder()
                .c1(41).build();

        int result = viewTestService.delete(tbTestDto);
        model.addAttribute("result", result);
        return PAGE_BASE_PATH + "simpleModelView";
    }

    @RequestMapping("/i18n")
    public String i18n(Model model) throws Exception {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        String connectTime = getI18nMessage("connectTime", new String[]{formattedDateTime});
        model.addAttribute("connectTime", connectTime);
        return PAGE_BASE_PATH + "i18n";
    }

    @AnoRequestDeduplication
    @RequestMapping("/duplicatedRequest")
    public String duplicatedRequest(Model model) throws Exception {
        //테스트를 위한 강제 딜레이
        Thread.sleep(3000L);
        return PAGE_BASE_PATH + "welcome";
    }

    //thyleaf 에러 처리 테스트
    @GetMapping("/validationWithBindingResult")
    public String validationWithBindingResult(ValidationTestDto validationTestDto) {
        log.debug("called by GET");
        return PAGE_BASE_PATH + "validationWithBindingResult";
    }

    @PostMapping("/validationWithBindingResult")
    public String validationWithBindingResult(@Valid ValidationTestDto validationTestDto, BindingResult bindingResult) {
        log.debug("called by POST");

        if (bindingResult.hasErrors()) {
            return PAGE_BASE_PATH + "validationWithBindingResult";
        }

        if (StringUtils.hasText(validationTestDto.getEmail()) && validationTestDto.getEmail().contains("@naver.com")) {
            bindingResult.rejectValue("email", "emailFail", "네이버 메일은 사용할 수 없습니다.");
            return PAGE_BASE_PATH + "validationWithBindingResult";
        }

        //do what you want.
        return "redirect:" + "validationWithBindingResult";
    }
}
