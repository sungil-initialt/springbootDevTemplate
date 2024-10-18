package com.sptek.webfw.example.web.test;

import com.sptek.webfw.anotation.AnoInterceptorCheck;
import com.sptek.webfw.anotation.AnoRequestDeduplication;
import com.sptek.webfw.common.code.ServiceErrorCodeEnum;
import com.sptek.webfw.common.exception.ServiceException;
import com.sptek.webfw.example.api.api1.ApiTestService;
import com.sptek.webfw.example.dto.*;
import com.sptek.webfw.support.CommonControllerSupport;
import com.sptek.webfw.support.PageInfoSupport;
import com.sptek.webfw.util.ModelMapperUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
@RequestMapping(value = "", produces = MediaType.TEXT_HTML_VALUE)
public class ViewTestController extends CommonControllerSupport {
    private final String baseUrl = "pages/example/test/";
    @Autowired
    private ViewTestService viewTestService;
    @Autowired
    private ApiTestService apiTestService;

    //기본 테스트
    //@RequestMapping({"/", "/welcome"})
    @RequestMapping({"/welcome"})
    public String welcome(Model model) {
        model.addAttribute("message", "welcome");
        return baseUrl + "welcome";
    }

    @RequestMapping("/interceptor")
    @AnoInterceptorCheck
    public String interceptor(Model model) {
        log.debug("ran origin caller here.");
        model.addAttribute("message", "welcome");
        return baseUrl + "interceptor";
    }

    //내부 로직에직 발생한 EX에 대한 처리.
    @RequestMapping("/serviceErr")
    public String serviceErr(Model model) {
        //if(1==1) throw new NullPointerException("NP Exception for Test");
        if(1==1) throw new ServiceException(ServiceErrorCodeEnum.SERVICE_DEFAULT_ERROR);

        return baseUrl + "xx"; //not to reach here
    }

    //Mybatis 를 통한 DB 테스트들
    @RequestMapping("/dbConnect")
    public String dbConnect(Model model) {
        int result = viewTestService.returnOne();
        model.addAttribute("result", result);
        return baseUrl + "simpleModelView";
    }

    @RequestMapping("/replicationMaster")
    public String replicationMaster(Model model) {
        int result = viewTestService.replicationMaster();
        model.addAttribute("result", result);
        return baseUrl + "simpleModelView";
    }

    @RequestMapping("/replicationSlave")
    public String replicationSlave(Model model) {
        int result = viewTestService.replicationSlave();
        model.addAttribute("result", result);
        return baseUrl + "simpleModelView";
    }

    @RequestMapping("/selectOne")
    public String selectOne(Model model) {
        TBTestDto tbTestDto = viewTestService.selectOne();
        model.addAttribute("result", tbTestDto.toString());
        return baseUrl + "simpleModelView";
    }

    @RequestMapping("/selectList")
    public String selectList(Model model) {
        List<TBTestDto> tbTestDtos = viewTestService.selectList();
        model.addAttribute("result", tbTestDtos.toString());
        return baseUrl + "simpleModelView";
    }

    @RequestMapping("/selectListWithResultHandler")
    //ResultHandler를 이용해서 db에서 result row를 하나씩 읽어와 각 row에 대한 처리가 가능함
    public String selectListWithResultHandler(Model model) {
        List<TBZipcodeDto> tBZipcode = viewTestService.selectListWithResultHandler();
        model.addAttribute("result", tBZipcode.toString());
        return baseUrl + "simpleModelView";
    }

    @RequestMapping("/selectMap")
    //result 결과 list를 map 형태로 받아올수 있다.
    public String selectMap(Model model) {
        Map<?, ?> resultMap = viewTestService.selectMap();
        model.addAttribute("result", resultMap.toString());
        return baseUrl + "simpleModelView";
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
        return baseUrl + "simpleModelView";
    }

    @RequestMapping("/insert")
    public String insert(Model model) {
        TBTestDto tbTestDto = TBTestDto.builder()
                .c1(41)
                .c2(42)
                .c3(43).build();

        int result = viewTestService.insert(tbTestDto);
        model.addAttribute("result", result);
        return baseUrl + "simpleModelView";
    }

    @RequestMapping("/update")
    public String update(Model model) {
        TBTestDto tbTestDto = TBTestDto.builder()
                .c1(41)
                .c2(422)
                .c3(433).build();

        int result = viewTestService.update(tbTestDto);
        model.addAttribute("result", result);
        return baseUrl + "simpleModelView";
    }

    @RequestMapping("/delete")
    public String delete(Model model) {
        TBTestDto tbTestDto = TBTestDto.builder()
                .c1(41).build();

        int result = viewTestService.delete(tbTestDto);
        model.addAttribute("result", result);
        return baseUrl + "simpleModelView";
    }

    @RequestMapping("/i18n")
    public String i18n(Model model) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        String connectTime = getI18nMessage("connectTime", new String[]{formattedDateTime});
        model.addAttribute("connectTime", connectTime);
        return baseUrl + "i18n";
    }

    @AnoRequestDeduplication
    @RequestMapping("/duplicatedRequest")
    public String duplicatedRequest(Model model) throws Exception {
        //테스트를 위한 강제 딜레이
        Thread.sleep(3000L);
        return baseUrl + "welcome";
    }

    //thyleaf 에러 처리 테스트
    @GetMapping("/validationWithBindingResult")
    public String validationWithBindingResult(ValidationTestDto validationTestDto) {
        //thyleaf 쪽에 default 값을 만들기 위해 validationTestDto 필요함

        log.debug("called by GET");
        return baseUrl + "validationWithBindingResult";
    }

    @PostMapping("/validationWithBindingResult")
    public String validationWithBindingResult(@Valid ValidationTestDto validationTestDto, BindingResult bindingResult) {
        log.debug("called by POST");

        if (bindingResult.hasErrors()) {
            return baseUrl + "validationWithBindingResult";
        }

        if (StringUtils.hasText(validationTestDto.getEmail()) && validationTestDto.getEmail().contains("@naver.com")) {
            bindingResult.rejectValue("email", "emailFail", "네이버 메일은 사용할 수 없습니다.");
            return baseUrl + "validationWithBindingResult";
        }

        //do what you want.
        return "redirect:" + "validationWithBindingResult";
    }

    //todo : Document에 대한 cache 처리를 위한건데.. 현재 cache 가 동작하지 않음(이유 확인이 필요함)
    @RequestMapping("/httpCache")
    public String httpCache(HttpServletResponse response, Model model) {
        long cacheSec = 60L;
        CacheControl cacheControl = CacheControl.maxAge(cacheSec, TimeUnit.SECONDS).cachePublic();
        long result = System.currentTimeMillis();

        model.addAttribute("result", result);
        response.setHeader("Cache-Control", cacheControl.getHeaderValue());
        return baseUrl + "simpleModelView";
    }

    @RequestMapping("/modelMapperTest")
    public String mapperMultiObject(Model model) {
        ExampleProductDto exampleProductDto = ExampleProductDto.builder()
                .manufacturerName("samsung")
                .productName("TV")
                .productPrice(1000000L)
                .curDiscountRate(20)
                .quantity(10)
                .isAvailableReturn(true)
                .build();
        ExampleGoodsDto exampleGoodsDto = ModelMapperUtil.map(exampleProductDto, ExampleGoodsDto.class);
        ExampleGoodsNProductDto exampleGoodsNProductDto = ModelMapperUtil.map(exampleProductDto, ExampleGoodsNProductDto.class);

        ExampleADto exampleADto = ExampleADto.builder().aDtoLastName("이").aDtoFirstName("성일").build();
        ExampleBDto exampleBDto = ModelMapperUtil.map(exampleADto, ExampleBDto.class);

        Map result = new HashMap();
        result.put("exampleProductDto", exampleProductDto);
        result.put("exampleGoodsDto", exampleGoodsDto);
        result.put("exampleGoodsNProductDto", exampleGoodsNProductDto);
        result.put("exampleADto", exampleADto);
        result.put("exampleBDto", exampleBDto);
        model.addAttribute("result", result);

        return baseUrl + "simpleModelView";
    }

    @RequestMapping("/viewServiceError")
    public String viewServiceError(@RequestParam("errorType") int errorType, Model model) {
        int result = apiTestService.raiseServiceError(errorType);
        model.addAttribute("result", result);
        return baseUrl + "simpleModelView";
    }
}

/*
-->
예전에 만들었던 객체들에서 @Builder 가 붙은 것들에 대해 잘 만들어 졌는지 확인이 필요함
ModelMapper 마무리 필요 (of 가 새 클레스 리턴전 후처리 작업을 할수 있는 람다 메소드를 지원하도록 수정 필요)

*/