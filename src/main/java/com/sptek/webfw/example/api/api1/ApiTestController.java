package com.sptek.webfw.example.api.api1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sptek.webfw.argumentResolver.AnoCustomArgument;
import com.sptek.webfw.argumentResolver.ArgumentResolverForMyUser;
import com.sptek.webfw.code.ApiSuccessCode;
import com.sptek.webfw.vo.PropertyVos;
import com.sptek.webfw.dto.ApiSuccessResponse;
import com.sptek.webfw.example.dto.ValidationTestDto;
import com.sptek.webfw.support.CloseableHttpClientSupport;
import com.sptek.webfw.support.RestTemplateSupport;
import com.sptek.webfw.util.ReqResUtil;
import com.sptek.webfw.util.TypeConvertUtil;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
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
//v1, v2 경로로 모두 접근 가능, produces를 통해 MediaType을 정할수 있으며 Agent가 해당 타입을 보낼때만 응답함. (TODO : xml 타입에 응답할수 있도록 처리 필요)
@RequestMapping(value = {"/api/v1/", "/api/v2/"}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
//swagger
@Tag(name = "기본정보", description = "테스트를 위한 기본 api 그룹")
public class ApiTestController {
    String fooResponseUrl = "https://worldtimeapi.org/api/timezone/Asia/Seoul"; //아무 의미없는 사이트로 단순히 rest 응답을 주는 테스트용 서버가 필요했음

    @Autowired
    private PropertyVos.ProjectInfoVo projectInfoVo;
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

    @Autowired
    private ApiTestService apiTestService;

    @GetMapping("/hello")
    @Operation(summary = "hello", description = "hello 테스트", tags = {""}) //swagger
    protected ResponseEntity<ApiSuccessResponse<String>> hello(
            @Parameter(name = "message", description = "ehco 로 응답할 내용", required = true) //swagger
            @RequestParam String message) {
        log.info("called hello");

        return new ResponseEntity<>(new ApiSuccessResponse<>(ApiSuccessCode.DEFAULT_SUCCESS, message)
                , ApiSuccessCode.DEFAULT_SUCCESS.getHttpStatusCode());
    }

    @GetMapping("/projectinfo")
    @Operation(summary = "프로젝트 정보", description = "projectinfo 테스트", tags = {""})
    //단순 프로젝트 정보 확인
    protected ResponseEntity<ApiSuccessResponse<PropertyVos.ProjectInfoVo>> projectinfo() {
        log.info("called projectinfo");

        return new ResponseEntity<>(new ApiSuccessResponse<>(ApiSuccessCode.DEFAULT_SUCCESS, projectInfoVo)
                , ApiSuccessCode.DEFAULT_SUCCESS.getHttpStatusCode());
    }

    @GetMapping("/XssProtectSupportGet")
    @Operation(summary = "XssProtectSupportGet", description = "XssProtectSupportGet 테스트", tags = {""})
    //get Req에 대한 xss 처리 결과 확인
    protected ResponseEntity<ApiSuccessResponse<String>> XssProtectSupportGet(
            @Parameter(name = "originText", description = "원본 텍스트", required = true)
            @RequestParam String originText) {
        log.info("called XssProtectSupportGet");

        String message = "XssProtectedText = " + originText;
        //XssProtectFilter를 통해 response body 내 스크립트 일괄 제거.
        return new ResponseEntity<>(new ApiSuccessResponse<>(ApiSuccessCode.DEFAULT_SUCCESS, message)
                , ApiSuccessCode.DEFAULT_SUCCESS.getHttpStatusCode());
    }

    @PostMapping("/XssProtectSupportPost")
    @Operation(summary = "XssProtectSupportPost", description = "XssProtectSupportPost 테스트", tags = {""})
    //post Req에 대한 xss 처리 결과 확인
    protected ResponseEntity<ApiSuccessResponse<String>> XssProtectSupportPost(
            @Parameter(name = "originText", description = "원본 텍스트", required = true)
            @RequestBody String originText) {
        log.info("called XssProtectSupportPost");

        String message = "XssProtectedText = " + originText;
        return new ResponseEntity<>(new ApiSuccessResponse<>(ApiSuccessCode.DEFAULT_SUCCESS, message)
                , ApiSuccessCode.DEFAULT_SUCCESS.getHttpStatusCode());
    }

    @RequestMapping("/xxInterceptorTest")
    @Operation(summary = "xxInterceptorTest", description = "xxInterceptorTest 테스트", tags = {""})
    //request의 method까지 구분해서 인터셉터가 적용되는지 단순히 확인하기 위한 용도로 관련 설정은 WebMvcConfig 파일에 있다.
    protected ResponseEntity<ApiSuccessResponse<String>> xxInterceptorTest() {
        log.info("called xxInterceptorTest");

        String message = "see the xxInterceptor message in log";
        return new ResponseEntity<>(new ApiSuccessResponse<>(ApiSuccessCode.DEFAULT_SUCCESS, message)
                , ApiSuccessCode.DEFAULT_SUCCESS.getHttpStatusCode());
    }

    @RequestMapping("/closeableHttpClient")
    @Operation(summary = "closeableHttpClient", description = "closeableHttpClient 테스트", tags = {""})
    //reqConfig와 pool이 이미 설정된 closeableHttpClient Bean을 사용하여 req 요청
    protected ResponseEntity<ApiSuccessResponse<String>> closeableHttpClient() throws Exception{
        log.info("called closeableHttpClient");
        log.info("closeableHttpClient identityHashCode : {}", System.identityHashCode(closeableHttpClient));

        HttpGet httpGet = new HttpGet(fooResponseUrl);
        httpGet.addHeader("X-test-id", "xyz");
        CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);

        HttpEntity httpEntity = closeableHttpResponse.getEntity();

        return new ResponseEntity<>(new ApiSuccessResponse<>(ApiSuccessCode.DEFAULT_SUCCESS, EntityUtils.toString(httpEntity))
                , ApiSuccessCode.DEFAULT_SUCCESS.getHttpStatusCode());
    }

    @RequestMapping("/closeableHttpClientSupport")
    @Operation(summary = "closeableHttpClientSupport", description = "closeableHttpClientSupport 테스트", tags = {""})
    //reqConfig와 pool이 이미 설정된 closeableHttpClient Bean을 사용하는, 좀더 사용성을 편리하게 만든 closeableHttpClientSupport 사용하는 req 요청
    protected ResponseEntity<ApiSuccessResponse<String>> closeableHttpClientSupport() throws Exception{
        log.info("called closeableHttpClientSupport");
        //log.info("closeableHttpClientSupport identityHashCode : {}", System.identityHashCode(closeableHttpClientSupport));

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(fooResponseUrl);
        HttpEntity httpEntity = closeableHttpClientSupport.requestPost(uriComponentsBuilder.toUriString(), null, null);

        return new ResponseEntity<>(new ApiSuccessResponse<>(ApiSuccessCode.DEFAULT_SUCCESS, CloseableHttpClientSupport.convertResponseToString(httpEntity))
                , ApiSuccessCode.DEFAULT_SUCCESS.getHttpStatusCode());
    }

    @RequestMapping("/restTemplate")
    @Operation(summary = "restTemplate", description = "restTemplate 테스트", tags = {""})
    //reqConfig와 pool이 이미 설정된 restTemplate Bean을 사용하여 req 요청
    protected ResponseEntity<ApiSuccessResponse<String>> restTemplate() {
        log.info("called restTemplate");

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(fooResponseUrl);
        String finalUrl = builder.toUriString();
        RequestEntity<Void> requestEntity = RequestEntity
                .method(HttpMethod.GET, finalUrl)
                .build();

        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
        return new ResponseEntity<>(new ApiSuccessResponse<>(ApiSuccessCode.DEFAULT_SUCCESS, responseEntity.getBody())
                , ApiSuccessCode.DEFAULT_SUCCESS.getHttpStatusCode());
    }

    @RequestMapping("/restTemplateSupport")
    @Operation(summary = "restTemplateSupport", description = "restTemplateSupport 테스트", tags = {""})
    //reqConfig와 pool이 이미 설정된 restTemplate Bean을 사용하는, 좀더 사용성을 편리하게 만든 restTemplateSupport 사용하는 req 요청
    protected ResponseEntity<ApiSuccessResponse<String>> restTemplateSupport() {
        log.info("called restTemplateSupport");

        ResponseEntity<String> responseEntity = restTemplateSupport.requestGet(fooResponseUrl, null, null);
        return new ResponseEntity<>(new ApiSuccessResponse<>(ApiSuccessCode.DEFAULT_SUCCESS, responseEntity.getBody())
                , ApiSuccessCode.DEFAULT_SUCCESS.getHttpStatusCode());
    }

    @RequestMapping("/reqResUtil")
    @Operation(summary = "reqResUtil", description = "reqResUtil 테스트", tags = {""})
    //ReqResUtil 검증 테스트
    protected ResponseEntity<ApiSuccessResponse<Map<String, String>>> reqResUtil(HttpServletRequest request) {
        log.info("called reqResUtil");

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
                , ApiSuccessCode.DEFAULT_SUCCESS.getHttpStatusCode());
    }

    @PostMapping("/validationAnnotationPost")
    @Operation(summary = "validationAnnotationPost", description = "validationAnnotationPost 테스트", tags = {""})
    //request input 값에대한 validation 처리 테스트
    protected ResponseEntity<ApiSuccessResponse<ValidationTestDto>> validationAnnotationPost(@RequestBody @Validated ValidationTestDto validationTestDto) {
        log.info("called validationAnnotationPost");

        return new ResponseEntity<>(new ApiSuccessResponse<>(ApiSuccessCode.DEFAULT_SUCCESS
                , validationTestDto)
                , ApiSuccessCode.DEFAULT_SUCCESS.getHttpStatusCode());
    }

    @GetMapping("/validationAnnotationGet")
    @Operation(summary = "validationAnnotationGet", description = "validationAnnotationGet 테스트", tags = {""})
    protected ResponseEntity<ApiSuccessResponse<ValidationTestDto>> validationAnnotationGet(@Validated ValidationTestDto validationTestDto) {
        log.info("called validationAnnotationGet");

        return new ResponseEntity<>(new ApiSuccessResponse<>(ApiSuccessCode.DEFAULT_SUCCESS
                , validationTestDto)
                , ApiSuccessCode.DEFAULT_SUCCESS.getHttpStatusCode());
    }

    @GetMapping("/propertyConfigImport")
    @Operation(summary = "propertyConfigImport", description = "propertyConfigImport 테스트", tags = {""})
    //컨트롤러 진입시 특정 property값을 가져올수 있다.
    protected ResponseEntity<ApiSuccessResponse<String>> propertyConfigImport(@Value("${specific.value}") String specificValue) {
        log.info("called propertyConfigImport");

        return new ResponseEntity<>(new ApiSuccessResponse<>(ApiSuccessCode.DEFAULT_SUCCESS
                , specificValue)
                , ApiSuccessCode.DEFAULT_SUCCESS.getHttpStatusCode());
    }

    @GetMapping("/argumentResolverForMyUser")
    @Operation(summary = "argumentResolverForMyUser", description = "argumentResolverForMyUser 테스트", tags = {""})
    //HandlerMethodArgumentResolver 를 implement 한 ArgumentResolverForMyUser에 의해 ArgumentResolverForMyUser.MyUser에 데이터가 바인딩 될때 미리 코딩된 로직에 따라 변형처리 되어 바인딩 할수 있다.
    //HandlerMethodArgumentResolver 의 구현체는 WebMvcConfig의 addArgumentResolvers()를 통해 미리 등록해 놓아야 한다. 등록되지 않으면 그냥 DTO로써 동일 네임 필드에 대해서만 1:1 바인딩 처리됨.
    protected ResponseEntity<ApiSuccessResponse<ArgumentResolverForMyUser.MyUser>> argumentResolverForMyUser(ArgumentResolverForMyUser.MyUser myUser) {
        //ArgumentResolverForMyUser에 어노테이션까지 일치해야 하는 조건이 들어 있기 때문에 resolveArgument()를 타지않고 단순 DTO로써의 역할만 처리됨
        log.info("called argumentResolverForMyUser");

        return new ResponseEntity<>(new ApiSuccessResponse<>(ApiSuccessCode.DEFAULT_SUCCESS
                , myUser)
                , ApiSuccessCode.DEFAULT_SUCCESS.getHttpStatusCode());
    }

    @GetMapping("/argumentResolverForMyUser2")
    @Operation(summary = "argumentResolverForMyUser2", description = "argumentResolverForMyUser2 테스트", tags = {""})
    protected ResponseEntity<ApiSuccessResponse<ArgumentResolverForMyUser.MyUser>> argumentResolverForMyUser2(@AnoCustomArgument ArgumentResolverForMyUser.MyUser myUser) {
        //어노테이션 조건까지 일치함으로 DTO의 단순 바인딩이 아니라 resolveArgument() 내부 코드가 처리해줌
        log.info("called argumentResolverForMyUser2");

        return new ResponseEntity<>(new ApiSuccessResponse<>(ApiSuccessCode.DEFAULT_SUCCESS
                , myUser)
                , ApiSuccessCode.DEFAULT_SUCCESS.getHttpStatusCode());
    }
}
