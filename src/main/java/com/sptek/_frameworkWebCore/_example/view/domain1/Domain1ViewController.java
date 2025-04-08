package com.sptek._frameworkWebCore._example.view.domain1;

import com.sptek._frameworkWebCore._example.api.domain1.Domain1ApiService;
import com.sptek._frameworkWebCore._example.dto.*;
import com.sptek._frameworkWebCore.annotation.EnableResponseOfViewGlobalException_InViewController;
import com.sptek._frameworkWebCore.annotation.TestAnnotation_InAll;
import com.sptek._frameworkWebCore.base.exception.ServiceException;
import com.sptek._frameworkWebCore.util.LocaleUtil;
import com.sptek._frameworkWebCore.util.ModelMapperUtil;
import com.sptek._frameworkWebCore.util.SecurityUtil;
import com.sptek._projectCommon.code.ServiceErrorCodeEnum;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor
@RequestMapping(value = "", produces = MediaType.TEXT_HTML_VALUE)
@EnableResponseOfViewGlobalException_InViewController
public class Domain1ViewController {
    @NonFinal
    private final String pageBasePath = "pages/example/test/";
    private final Domain1ViewService domain1ViewService;
    private final Domain1ApiService domain1ApiService;

    @GetMapping({"/pageForApiTestWithFetch"})
    public String pageForFetchTest() {
        return pageBasePath + "pageForApiTestWithFetch";
    }

    //기본 테스트
    //@RequestMapping({"/", "/welcome"})
    @GetMapping({"/", "/welcome"})
    public String welcome(Model model) {
        model.addAttribute("message", "welcome");
        return pageBasePath + "welcome";
    }

    //기본 테스트
    @PostMapping({"/postUrl"})
    public String postUrl(Model model) {
        model.addAttribute("message", "postUrl");
        return pageBasePath + "welcome";
    }



    @RequestMapping("/interceptor")
    @TestAnnotation_InAll
    public String interceptor(Model model) {
        log.debug("origin caller here.");
        model.addAttribute("message", "welcome");
        return pageBasePath + "interceptor";
    }

    //내부 로직에직 발생한 EX에 대한 처리.
    @RequestMapping("/serviceErr")
    public String serviceErr(Model model) {
        //if(1==1) throw new NullPointerException("NP Exception for Test");
        if(1==1) throw new ServiceException(ServiceErrorCodeEnum.XXX_ERROR);

        return pageBasePath + "xx"; //not to reach here
    }










    @RequestMapping("/i18n")
    public String i18n(Model model) {
        ZonedDateTime zonedDateTimeForSystem = ZonedDateTime.now(ZoneId.systemDefault());
        ZonedDateTime zonedDateTimeForUser = ZonedDateTime.now(LocaleUtil.getCurTimeZone().toZoneId());

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String systemFormattedDateTime = zonedDateTimeForSystem.format(dateTimeFormatter);
        String userFormattedDateTime = zonedDateTimeForUser.format(dateTimeFormatter);

        //Controller 에서 다국어 변환을 직접 하는 케이스
        String welcome = LocaleUtil.getI18nMessage("welcome", SecurityUtil.getUserAuthentication().getName());
        String language = LocaleUtil.getI18nMessage("language");

        String userLanguageTag = LocaleUtil.getCurLanguageTag();
        String userTimeZone = LocaleUtil.getCurTimeZoneName();

        model.addAttribute("welcome", welcome);
        model.addAttribute("language", language);
        model.addAttribute("userLanguageTag", userLanguageTag);
        model.addAttribute("userTimeZone", userTimeZone);
        model.addAttribute("systemFormattedDateTime", systemFormattedDateTime);
        model.addAttribute("userFormattedDateTime", userFormattedDateTime);

        List<LocaleUtil.LocaleDto> localeDtos = LocaleUtil.getMajorLocales();
        for(LocaleUtil.LocaleDto localeDto : localeDtos) {
            log.debug(localeDto.toString() + " : " + localeDto.toLocaleParam());
        }

        return pageBasePath + "i18n";
    }

    //thyleaf 에러 처리 테스트
    @GetMapping("/validationWithBindingResult")
    public String validationWithBindingResult(ValidatedDto validationTestDto) {
        //thyleaf 쪽에 default 값을 만들기 위해 validationTestDto 필요함

        log.debug("called by GET");
        return pageBasePath + "validationWithBindingResult";
    }

    @PostMapping("/validationWithBindingResult")
    public String validationWithBindingResult(@Valid ValidatedDto validationTestDto, BindingResult bindingResult) {
        log.debug("called by POST");

        if (bindingResult.hasErrors()) {
            return pageBasePath + "validationWithBindingResult";
        }

        if (StringUtils.hasText(validationTestDto.getEmail()) && validationTestDto.getEmail().contains("@naver.com")) {
            bindingResult.rejectValue("email", "emailFail", "네이버 메일은 사용할 수 없습니다.");
            return pageBasePath + "validationWithBindingResult";
        }

        //do what you want.
        return "redirect:" + "validationWithBindingResult";
    }

    //todo : Document에 대한 cache 처리를 위한건데.. 현재 cache 가 동작하지 않음(이유 확인이 필요함)
    @RequestMapping("/httpCache")
    public String httpCache(HttpServletResponse response, Model model) {
        long result = System.currentTimeMillis();

        // CacheControl을 이용한 캐시 헤더 설정
        CacheControl cacheControl = CacheControl.maxAge(60L, TimeUnit.SECONDS)  // 60초 동안 캐시
                .cachePublic()  // 공용 캐시 가능
                ;//.mustRevalidate();  // 만료된 경우 재검증 필요

        model.addAttribute("result", result);
        response.setHeader("Cache-Control", cacheControl.getHeaderValue());

        return pageBasePath + "simpleModelView";
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

        //example 1
        ExampleGoodsDto exampleGoodsDto = ModelMapperUtil.map(exampleProductDto, ExampleGoodsDto.class);

        //example 2
        ExampleGoodsNProductDto exampleGoodsNProductDto = ModelMapperUtil.map(exampleProductDto, ExampleGoodsNProductDto.class);

        ExampleADto exampleADto = ExampleADto.builder()
                .aDtoLastName("이")
                .aDtoFirstName("성일")
                .build();

        //example 3
        ExampleBDto exampleBDto = ModelMapperUtil.map(exampleADto, ExampleBDto.class);

        Map<String, Object> result = new HashMap();
        result.put("ExampleProductDto-origin", exampleProductDto);
        result.put("ExampleProductDto-exampleGoodsDto", exampleGoodsDto);
        result.put("ExampleProductDto-exampleGoodsNProductDto", exampleGoodsNProductDto);

        result.put("ExampleADto-origin", exampleADto);
        result.put("ExampleADto-exampleBDto", exampleBDto);
        model.addAttribute("result", result);

        return pageBasePath + "simpleModelView";
    }

    @RequestMapping("/viewServiceError")
    public String viewServiceError(@RequestParam("errorCaseNum") int errorCaseNum, Model model) {
        int result = domain1ApiService.raiseServiceError(errorCaseNum);
        model.addAttribute("result", result);
        return pageBasePath + "simpleModelView";
    }
}

/*
-->
예전에 만들었던 객체들에서 @Builder 가 붙은 것들에 대해 잘 만들어 졌는지 확인이 필요함
ModelMapper 마무리 필요 (of 가 새 클레스 리턴전 후처리 작업을 할수 있는 람다 메소드를 지원하도록 수정 필요)

*/