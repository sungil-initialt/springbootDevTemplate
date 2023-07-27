package com.sptek.webfw.example.api.api1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sptek.webfw.code.ApiSuccessCode;
import com.sptek.webfw.config.vo.PropertyVos;
import com.sptek.webfw.dto.ApiSuccessResponse;
import com.sptek.webfw.example.dto.ValidationTestDto;
import com.sptek.webfw.support.CloseableHttpClientSupport;
import com.sptek.webfw.support.RestTemplateSupport;
import com.sptek.webfw.util.ReqResUtil;
import com.sptek.webfw.util.TypeConvertUtil;
import com.sun.jna.StringArray;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/", produces = "application/json")
@Tag(name = "기본정보", description = "테스트를 위한 기본 api 그룹")
public class ApiTestController {
    String restTestUrl = "http://worldtimeapi.org/api/timezone/Asia/Seoul";

    @Autowired
    private PropertyVos.ProjectInfo projectInfo;
    @Autowired
    CloseableHttpClient closeableHttpClient;
    @Autowired
    CloseableHttpClientSupport closeableHttpClientSupport;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    RestTemplateSupport restTemplateSupport;
    @Autowired
    ObjectMapper objectMapper;

    @GetMapping("/hello")
    @Operation(summary = "hello", description = "hello 테스트", tags = {""})
    protected ResponseEntity<ApiSuccessResponse<String>> hello(
            @Parameter(name = "message", description = "ehco 로 응답할 내용", required = true)
            @RequestParam String message) {
        log.error("called hello");

        return new ResponseEntity<>(new ApiSuccessResponse<>(ApiSuccessCode.DEFAULT_SUCCESS, message)
                ,ApiSuccessCode.DEFAULT_SUCCESS.getHttpStatusCode());
    }

    @GetMapping("/projectinfo")
    @Operation(summary = "프로젝트 정보", description = "projectinfo 테스트", tags = {""})
    protected ResponseEntity<ApiSuccessResponse<PropertyVos.ProjectInfo>> projectinfo() {
        log.error("called projectinfo");

        return new ResponseEntity<>(new ApiSuccessResponse<>(ApiSuccessCode.DEFAULT_SUCCESS, projectInfo)
                ,ApiSuccessCode.DEFAULT_SUCCESS.getHttpStatusCode());
    }

    @GetMapping("/XssProtectSupportGet")
    @Operation(summary = "XssProtectSupportGet", description = "XssProtectSupportGet 테스트", tags = {""})
    protected ResponseEntity<ApiSuccessResponse<String>> XssProtectSupportGet(
            @Parameter(name = "originText", description = "원본 텍스트", required = true)
            @RequestParam String originText) {
        log.error("called XssProtectSupportGet");

        String message = "XssProtectedText = " + originText;
        return new ResponseEntity<>(new ApiSuccessResponse<>(ApiSuccessCode.DEFAULT_SUCCESS, message)
                ,ApiSuccessCode.DEFAULT_SUCCESS.getHttpStatusCode());
    }

    @PostMapping("/XssProtectSupportPost")
    @Operation(summary = "XssProtectSupportPost", description = "XssProtectSupportPost 테스트", tags = {""})
    protected ResponseEntity<ApiSuccessResponse<String>> XssProtectSupportPost(
            @Parameter(name = "originText", description = "원본 텍스트", required = true)
            @RequestBody String originText) {
        log.error("called XssProtectSupportPost");

        String message = "XssProtectedText = " + originText;
        return new ResponseEntity<>(new ApiSuccessResponse<>(ApiSuccessCode.DEFAULT_SUCCESS, message)
                ,ApiSuccessCode.DEFAULT_SUCCESS.getHttpStatusCode());
    }

    @RequestMapping("/xxInterceptorTest")
    @Operation(summary = "xxInterceptorTest", description = "xxInterceptorTest 테스트", tags = {""})
    protected ResponseEntity<ApiSuccessResponse<String>> xxInterceptorTest() {
        log.error("called xxInterceptorTest");

        String message = "see the xxInterceptor message in log";
        return new ResponseEntity<>(new ApiSuccessResponse<>(ApiSuccessCode.DEFAULT_SUCCESS, message)
                ,ApiSuccessCode.DEFAULT_SUCCESS.getHttpStatusCode());
    }

    @RequestMapping("/closeableHttpClient")
    @Operation(summary = "closeableHttpClient", description = "closeableHttpClient 테스트", tags = {""})
    protected ResponseEntity<ApiSuccessResponse<String>> closeableHttpClient() throws Exception{
        log.error("called closeableHttpClient");

        HttpGet httpGet = new HttpGet(restTestUrl);
        httpGet.addHeader("X-test-id", "xyz");
        CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);

        HttpEntity httpEntity = closeableHttpResponse.getEntity();

        return new ResponseEntity<>(new ApiSuccessResponse<>(ApiSuccessCode.DEFAULT_SUCCESS, EntityUtils.toString(httpEntity))
                ,ApiSuccessCode.DEFAULT_SUCCESS.getHttpStatusCode());
    }

    @RequestMapping("/closeableHttpClientSupport")
    @Operation(summary = "closeableHttpClientSupport", description = "closeableHttpClientSupport 테스트", tags = {""})
    protected ResponseEntity<ApiSuccessResponse<String>> closeableHttpClientSupport() throws Exception{
        log.error("called closeableHttpClientSupport");

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(restTestUrl);
        HttpEntity httpEntity = closeableHttpClientSupport.requestPost(uriComponentsBuilder.toUriString(), null, null);

        return new ResponseEntity<>(new ApiSuccessResponse<>(ApiSuccessCode.DEFAULT_SUCCESS, CloseableHttpClientSupport.convertResponseToString(httpEntity))
                ,ApiSuccessCode.DEFAULT_SUCCESS.getHttpStatusCode());
    }

    @RequestMapping("/restTemplate")
    @Operation(summary = "restTemplate", description = "restTemplate 테스트", tags = {""})
    protected ResponseEntity<ApiSuccessResponse<String>> restTemplate() {
        log.error("called restTemplate");

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(restTestUrl);
        String finalUrl = builder.toUriString();
        RequestEntity<Void> requestEntity = RequestEntity
                .method(HttpMethod.GET, finalUrl)
                .build();

        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
        return new ResponseEntity<>(new ApiSuccessResponse<>(ApiSuccessCode.DEFAULT_SUCCESS, responseEntity.getBody())
                ,ApiSuccessCode.DEFAULT_SUCCESS.getHttpStatusCode());
    }

    @RequestMapping("/restTemplateSupport")
    @Operation(summary = "restTemplateSupport", description = "restTemplateSupport 테스트", tags = {""})
    protected ResponseEntity<ApiSuccessResponse<String>> restTemplateSupport() {
        log.error("called restTemplateSupport");

        ResponseEntity<String> responseEntity = restTemplateSupport.requestGet(restTestUrl, null, null);
        return new ResponseEntity<>(new ApiSuccessResponse<>(ApiSuccessCode.DEFAULT_SUCCESS, responseEntity.getBody())
                ,ApiSuccessCode.DEFAULT_SUCCESS.getHttpStatusCode());
    }

    @RequestMapping("/reqResUtil")
    @Operation(summary = "reqResUtil", description = "reqResUtil 테스트", tags = {""})
    protected ResponseEntity<ApiSuccessResponse<Map<String, String>>> reqResUtil(HttpServletRequest request) {
        log.error("called reqResUtil");

        String reqFullUrl = ReqResUtil.getRequestUrlString(request);
        String reqIp = ReqResUtil.getReqUserIp(request);
        String heades = ReqResUtil.getRequestHeaderMap(request).toString();
        String params = TypeConvertUtil.strArrMapToString(ReqResUtil.getRequestParameterMap(request));

        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("reqFullUrl", reqFullUrl);
        resultMap.put("reqIp", reqIp);
        resultMap.put("heades", heades);
        resultMap.put("params", params);

        return new ResponseEntity<>(new ApiSuccessResponse<>(ApiSuccessCode.DEFAULT_SUCCESS
                , resultMap)
                ,ApiSuccessCode.DEFAULT_SUCCESS.getHttpStatusCode());
    }

    @PostMapping("/ValidationAnnotationPost")
    @Operation(summary = "ValidationAnnotationPost", description = "ValidationAnnotationPost 테스트", tags = {""})
    protected ResponseEntity<ApiSuccessResponse<ValidationTestDto>> ValidationAnnotationPost(@RequestBody @Validated ValidationTestDto validationTestDto) {
        log.error("called ValidationAnnotationPost");

        return new ResponseEntity<>(new ApiSuccessResponse<>(ApiSuccessCode.DEFAULT_SUCCESS
                , validationTestDto)
                ,ApiSuccessCode.DEFAULT_SUCCESS.getHttpStatusCode());
    }

    @GetMapping("/ValidationAnnotationGet")
    @Operation(summary = "ValidationAnnotationGet", description = "ValidationAnnotationGet 테스트", tags = {""})
    protected ResponseEntity<ApiSuccessResponse<ValidationTestDto>> ValidationAnnotationGet(@ModelAttribute  @Validated ValidationTestDto validationTestDto) {
        log.error("called ValidationAnnotationGet");

        return new ResponseEntity<>(new ApiSuccessResponse<>(ApiSuccessCode.DEFAULT_SUCCESS
                , validationTestDto)
                ,ApiSuccessCode.DEFAULT_SUCCESS.getHttpStatusCode());
    }

}
