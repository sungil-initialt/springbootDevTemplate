package com.sptek.webfw.example.api.api1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sptek.webfw.code.ApiSuccessCode;
import com.sptek.webfw.config.property.PropertyVos;
import com.sptek.webfw.dto.ApiSuccessResponse;
import com.sptek.webfw.support.CloseableHttpClientSupport;
import com.sptek.webfw.support.RestTemplateSupport;
import com.sptek.webfw.util.ReqResUtil;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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
    @Operation(summary = "프로젝트 에코", description = "프로젝트 동작을 모니터링 합니다..", tags = {""})
    protected ResponseEntity<ApiSuccessResponse<String>> hello(
            @Parameter(name = "ehco", description = "ehco 로 응답할 내용", required = true)
            @RequestParam String ehco) {
        log.error("called hello");

        return new ResponseEntity<>(new ApiSuccessResponse<>(ApiSuccessCode.DEFAULT_SUCCESS, "{'message':'" + ehco + "'}")
                ,ApiSuccessCode.DEFAULT_SUCCESS.getHttpStatusCode());
    }


    @GetMapping("/projectinfo")
    @Operation(summary = "프로젝트 정보", description = "프로젝트 정보를 조회 합니다.", tags = {""})
    protected ResponseEntity<ApiSuccessResponse<PropertyVos.ProjectInfo>> projectinfo() {
        log.error("called projectinfo");

        return new ResponseEntity<>(new ApiSuccessResponse<>(ApiSuccessCode.DEFAULT_SUCCESS, projectInfo)
                ,ApiSuccessCode.DEFAULT_SUCCESS.getHttpStatusCode());
    }

    @RequestMapping("/closeableHttpClient")
    @Operation(summary = "closeableHttpClient", description = "간단테스트", tags = {""})
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
    @Operation(summary = "closeableHttpClientSupport", description = "간단테스트", tags = {""})
    protected ResponseEntity<ApiSuccessResponse<String>> closeableHttpClientSupport() throws Exception{
        log.error("called closeableHttpClientSupport");

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(restTestUrl);
        HttpEntity httpEntity = closeableHttpClientSupport.requestPost(uriComponentsBuilder.toUriString(), null, null);

        return new ResponseEntity<>(new ApiSuccessResponse<>(ApiSuccessCode.DEFAULT_SUCCESS, CloseableHttpClientSupport.convertResponseToString(httpEntity))
                ,ApiSuccessCode.DEFAULT_SUCCESS.getHttpStatusCode());
    }

    @RequestMapping("/restTemplate")
    @Operation(summary = "restTemplate", description = "간단테스트", tags = {""})
    protected ResponseEntity<ApiSuccessResponse<String>> restTemplate() throws Exception{
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
    @Operation(summary = "restTemplateSupport", description = "간단테스트", tags = {""})
    protected ResponseEntity<ApiSuccessResponse<String>> restTemplateSupport() throws Exception{
        log.error("called restTemplateSupport");

        ResponseEntity<String> responseEntity = restTemplateSupport.requestGet(restTestUrl, null, null);
        return new ResponseEntity<>(new ApiSuccessResponse<>(ApiSuccessCode.DEFAULT_SUCCESS, responseEntity.getBody())
                ,ApiSuccessCode.DEFAULT_SUCCESS.getHttpStatusCode());
    }

    @RequestMapping("/reqResUtil")
    @Operation(summary = "reqResUtil", description = "간단테스트", tags = {""})
    protected ResponseEntity<ApiSuccessResponse<Map<String, String[]>>> reqResUtil(HttpServletRequest request) throws Exception{
        log.error("called reqResUtil");

        String reqFullUrl = ReqResUtil.getRequestUrlString(request);
        Map<String, String> headeMap = ReqResUtil.getRequestHeaderMap(request);
        Map<String, String[]> reqParamMap = ReqResUtil.getRequestParameterMap(request);
        String reqIp = ReqResUtil.getReqUserIp(request);


        return new ResponseEntity<>(new ApiSuccessResponse<>(ApiSuccessCode.DEFAULT_SUCCESS
                , ReqResUtil.getRequestParameterMap(request))
                ,ApiSuccessCode.DEFAULT_SUCCESS.getHttpStatusCode());
    }

}
