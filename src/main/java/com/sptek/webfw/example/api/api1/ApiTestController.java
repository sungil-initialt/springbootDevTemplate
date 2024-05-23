package com.sptek.webfw.example.api.api1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sptek.webfw.anotation.AnoCustomArgument;
import com.sptek.webfw.anotation.AnoRequestDeduplication;
import com.sptek.webfw.argumentResolver.ArgumentResolverForMyUser;
import com.sptek.webfw.common.responseDto.ApiSuccessResponseDto;
import com.sptek.webfw.example.dto.FileUploadDto;
import com.sptek.webfw.example.dto.ValidationTestDto;
import com.sptek.webfw.support.CloseableHttpClientSupport;
import com.sptek.webfw.support.CommonControllerSupport;
import com.sptek.webfw.support.RestTemplateSupport;
import com.sptek.webfw.util.FileUtil;
import com.sptek.webfw.util.ReqResUtil;
import com.sptek.webfw.util.TypeConvertUtil;
import com.sptek.webfw.vo.PropertyVos;
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
import org.springframework.http.*;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

@Slf4j
@RestController
//v1, v2 경로로 모두 접근 가능, produces를 통해 MediaType을 정할수 있으며 Agent가 해당 타입을 보낼때만 응답함. (TODO : xml로 응답하는 기능도 추가하면 좋을듯)
//@RequestMapping(value = {"/api/v1/", "/api/v2/"}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
@RequestMapping(value = {"/api/v1/"})
//swagger
@Tag(name = "기본정보", description = "테스트를 위한 기본 api 그룹")
public class ApiTestController extends CommonControllerSupport {
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
    @Operation(summary = "hello", description = "hello 테스트", tags = {"echo"}) //swagger
    public ResponseEntity<ApiSuccessResponseDto<String>> hello(
            @Parameter(name = "message", description = "ehco 로 응답할 내용", required = true) //swagger
            @RequestParam String message) throws Exception{

        return ResponseEntity.ok(new ApiSuccessResponseDto(message));
    }

    @GetMapping("/projectinfo")
    @Operation(summary = "projectinfo", description = "projectinfo 테스트", tags = {""})
    //단순 프로젝트 정보 확인
    public ResponseEntity<ApiSuccessResponseDto<PropertyVos.ProjectInfoVo>> projectinfo() {
        return ResponseEntity.ok(new ApiSuccessResponseDto(projectInfoVo));
    }

    @PostMapping("/swaggerExample")
    @Operation(summary = "swagger 구성 예시", description = "swagger 구성의 가이드 API", tags = {"가이드 예시 APIs"})
    public ResponseEntity<ApiSuccessResponseDto<ValidationTestDto>> swaggerExample(
            @Parameter(name = "message", description = "가이드용으로 의미가 없음", required = true)
            @RequestParam String message,
            @RequestBody ValidationTestDto validationTestDto) {

        return ResponseEntity.ok(new ApiSuccessResponseDto(validationTestDto));
    }

    @GetMapping("/XssProtectSupportGet")
    @Operation(summary = "XssProtectSupportGet", description = "XssProtectSupportGet 테스트", tags = {""})
    //get Req에 대한 xss 처리 결과 확인
    public ResponseEntity<ApiSuccessResponseDto<String>> XssProtectSupportGet(
            @Parameter(name = "originText", description = "원본 텍스트", required = true)
            @RequestParam String originText) {

        //XssProtectFilter를 통해 response body 내 스크립트 일괄 제거.
        String message = "XssProtectedText = " + originText;
        return ResponseEntity.ok(new ApiSuccessResponseDto(message));
    }

    @PostMapping("/XssProtectSupportPost")
    @Operation(summary = "XssProtectSupportPost", description = "XssProtectSupportPost 테스트", tags = {""})
    //post Req에 대한 xss 처리 결과 확인
    public ResponseEntity<ApiSuccessResponseDto<String>> XssProtectSupportPost(
            @Parameter(name = "originText", description = "원본 텍스트", required = true)
            @RequestBody String originText) {

        String message = "XssProtectedText = " + originText;
        return ResponseEntity.ok(new ApiSuccessResponseDto(message));
    }

    @RequestMapping("/interceptor")
    @Operation(summary = "interceptor", description = "interceptor 테스트", tags = {""})
    //인터셉터들을 테스트 하기 위한 용도
    public ResponseEntity<ApiSuccessResponseDto<String>> interceptor() {
        String message = "see the interceptor message in log";
        return ResponseEntity.ok(new ApiSuccessResponseDto(message));
    }

    @RequestMapping("/closeableHttpClient")
    @Operation(summary = "closeableHttpClient", description = "closeableHttpClient 테스트", tags = {""})
    //reqConfig와 pool이 이미 설정된 closeableHttpClient Bean을 사용하여 req 요청
    public ResponseEntity<ApiSuccessResponseDto<String>> closeableHttpClient() throws Exception{
        log.debug("identityHashCode : {}", System.identityHashCode(closeableHttpClient));

        HttpGet httpGet = new HttpGet(fooResponseUrl);
        httpGet.addHeader("X-test-id", "xyz");
        CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);

        HttpEntity httpEntity = closeableHttpResponse.getEntity();
        return ResponseEntity.ok(new ApiSuccessResponseDto(EntityUtils.toString(httpEntity)));
    }

    @RequestMapping("/closeableHttpClientSupport")
    @Operation(summary = "closeableHttpClientSupport", description = "closeableHttpClientSupport 테스트", tags = {""})
    //reqConfig와 pool이 이미 설정된 closeableHttpClient Bean을 사용하는, 좀더 사용성을 편리하게 만든 closeableHttpClientSupport 사용하는 req 요청
    public ResponseEntity<ApiSuccessResponseDto<String>> closeableHttpClientSupport() throws Exception{
        log.debug("identityHashCode : {}", System.identityHashCode(closeableHttpClientSupport));

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(fooResponseUrl);
        HttpEntity httpEntity = closeableHttpClientSupport.requestPost(uriComponentsBuilder.toUriString(), null, null);

        return ResponseEntity.ok(new ApiSuccessResponseDto(CloseableHttpClientSupport.convertResponseToString(httpEntity)));
    }

    @RequestMapping("/restTemplate")
    @Operation(summary = "restTemplate", description = "restTemplate 테스트", tags = {""})
    //reqConfig와 pool이 이미 설정된 restTemplate Bean을 사용하여 req 요청
    public ResponseEntity<ApiSuccessResponseDto<String>> restTemplate() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(fooResponseUrl);
        String finalUrl = builder.toUriString();
        RequestEntity<Void> requestEntity = RequestEntity
                .method(HttpMethod.GET, finalUrl)
                .build();

        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
        return ResponseEntity.ok(new ApiSuccessResponseDto(responseEntity.getBody()));
    }

    @RequestMapping("/restTemplateSupport")
    @Operation(summary = "restTemplateSupport", description = "restTemplateSupport 테스트", tags = {""})
    //reqConfig와 pool이 이미 설정된 restTemplate Bean을 사용하는, 좀더 사용성을 편리하게 만든 restTemplateSupport 사용하는 req 요청
    public ResponseEntity<ApiSuccessResponseDto<String>> restTemplateSupport() {
        ResponseEntity<String> responseEntity = restTemplateSupport.requestGet(fooResponseUrl, null, null);
        return ResponseEntity.ok(new ApiSuccessResponseDto(responseEntity.getBody()));
    }

    @RequestMapping("/reqResUtil")
    @Operation(summary = "reqResUtil", description = "reqResUtil 테스트", tags = {""})
    //ReqResUtil 검증 테스트
    public ResponseEntity<ApiSuccessResponseDto<Map<String, String>>> reqResUtil(HttpServletRequest request) {
        String reqFullUrl = ReqResUtil.getRequestUrlString(request);
        String reqIp = ReqResUtil.getReqUserIp(request);
        String heades = ReqResUtil.getRequestHeaderMap(request).toString();
        String params = TypeConvertUtil.strArrMapToString(ReqResUtil.getRequestParameterMap(request));

        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("reqFullUrl", reqFullUrl);
        resultMap.put("reqIp", reqIp);
        resultMap.put("heades", heades);
        resultMap.put("params", params);

        return ResponseEntity.ok(new ApiSuccessResponseDto(resultMap));
    }

    @PostMapping("/validationAnnotationPost")
    @Operation(summary = "validationAnnotationPost", description = "validationAnnotationPost 테스트", tags = {""})
    //request input 값에대한 validation 처리 테스트
    public ResponseEntity<ApiSuccessResponseDto<ValidationTestDto>> validationAnnotationPost(@RequestBody @Validated ValidationTestDto validationTestDto) {
        return ResponseEntity.ok(new ApiSuccessResponseDto(validationTestDto));
    }

    @GetMapping("/validationAnnotationGet")
    @Operation(summary = "validationAnnotationGet", description = "validationAnnotationGet 테스트", tags = {""})
    public ResponseEntity<ApiSuccessResponseDto<ValidationTestDto>> validationAnnotationGet(@Validated ValidationTestDto validationTestDto) {
        return ResponseEntity.ok(new ApiSuccessResponseDto(validationTestDto));
    }

    @GetMapping("/propertyConfigImport")
    @Operation(summary = "propertyConfigImport", description = "propertyConfigImport 테스트", tags = {""})
    //컨트롤러 진입시 특정 property값을 가져올수 있다.
    public ResponseEntity<ApiSuccessResponseDto<String>> propertyConfigImport(@Value("${specific.value}") String specificValue) {
        return ResponseEntity.ok(new ApiSuccessResponseDto(specificValue));
    }

    @GetMapping("/argumentResolverForMyUser")
    @Operation(summary = "argumentResolverForMyUser", description = "argumentResolverForMyUser 테스트", tags = {""})
    //HandlerMethodArgumentResolver 를 implement 한 ArgumentResolverForMyUser에 의해 ArgumentResolverForMyUser.MyUser에 데이터가 바인딩 될때 미리 코딩된 로직에 따라 변형처리 되어 바인딩 할수 있다.
    //HandlerMethodArgumentResolver 의 구현체는 WebMvcConfig의 addArgumentResolvers()를 통해 미리 등록해 놓아야 한다. 등록되지 않으면 그냥 DTO로써 동일 네임 필드에 대해서만 1:1 바인딩 처리됨.
    public ResponseEntity<ApiSuccessResponseDto<ArgumentResolverForMyUser.MyUser>> argumentResolverForMyUser(ArgumentResolverForMyUser.MyUser myUser) {
        //ArgumentResolverForMyUser에 어노테이션까지 일치해야 하는 조건이 들어 있기 때문에 resolveArgument()를 타지않고 단순 DTO로써의 역할만 처리됨
        return ResponseEntity.ok(new ApiSuccessResponseDto(myUser));
    }

    @GetMapping("/argumentResolverForMyUser2")
    @Operation(summary = "argumentResolverForMyUser2", description = "argumentResolverForMyUser2 테스트", tags = {""})
    public ResponseEntity<ApiSuccessResponseDto<ArgumentResolverForMyUser.MyUser>> argumentResolverForMyUser2(@AnoCustomArgument ArgumentResolverForMyUser.MyUser myUser) {
        //어노테이션 조건까지 일치함으로 DTO의 단순 바인딩이 아니라 resolveArgument() 내부 코드가 처리해줌
        return ResponseEntity.ok(new ApiSuccessResponseDto(myUser));
    }

    @PostMapping(value = "/fileUpload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "fileUpload", description = "fileUpload 테스트", tags = {""})
    public ResponseEntity<ApiSuccessResponseDto<List<FileUploadDto>>> fileUpload(@Value("${Storage.multipartFiles.basePath}") String baseStoragePath
            , @RequestParam("uploadFiles") MultipartFile[] uploadFiles
            , @RequestParam("fileDescription") String fileDescription) throws Exception{

        log.debug("file count = {}, fileDescription = {}", uploadFiles.length, fileDescription);
        //todo: 실제 상황에서는 부가정보 저장 처리등 조치 필요

        String additionalPath = ""; //로그인계정번호등 필요한 구분 디렉토리가 있는다면 추가
        Predicate<MultipartFile> exceptionFilter = multipartFile -> multipartFile.getContentType().startsWith("image") ? false : true; //ex를 발생시키는 조건 (필요에 따라 수정)
        List<FileUploadDto> FileUploadDtoList = FileUtil.saveMultipartFiles(uploadFiles, baseStoragePath, additionalPath, exceptionFilter);

        return ResponseEntity.ok(new ApiSuccessResponseDto(FileUploadDtoList));
    }

    @GetMapping(value = "/byteForImage")
    @Operation(summary = "byteForImage", description = "byteForImage 테스트", tags = {""})
    public ResponseEntity<byte[]> byteForImage(@Value("${Storage.multipartFiles.basePath}") String baseStoragePath
            , @RequestParam("originFileName") String originFileName
            , @RequestParam("uuidForFileName") String uuidForFileName) throws Exception{

        originFileName = URLDecoder.decode(originFileName, StandardCharsets.UTF_8);
        uuidForFileName = URLDecoder.decode(uuidForFileName, StandardCharsets.UTF_8);
        log.debug("originFileName = {}, uuidForFileName = {}", originFileName, uuidForFileName);

        //todo : 실제 상황에서는 uuid 값을 통해 저장 위치를 검색해오도록 수정 필요
        String realFilePath = baseStoragePath + File.separator +  LocalDate.now().getYear()
                + File.separator + LocalDate.now().getMonthValue()
                + File.separator + LocalDate.now().getDayOfMonth()
                + File.separator + uuidForFileName + "_" + originFileName;
        log.debug("realFilePath : {}", realFilePath);

        File imageFile = new File(realFilePath);
        HttpHeaders header = new HttpHeaders();
        header.add("Content-Type", Files.probeContentType(imageFile.toPath())); // MIME 타입 처리

        return new ResponseEntity<>(FileCopyUtils.copyToByteArray(imageFile), header, HttpStatus.OK);
    }

    @AnoRequestDeduplication
    @RequestMapping("/duplicatedRequest")
    @Operation(summary = "duplicatedRequest", description = "duplicatedRequest 테스트", tags = {""})
    public ResponseEntity<ApiSuccessResponseDto<String>> duplicatedRequest() throws Exception {
        String result = "duplicatedRequest test";
        Thread.sleep(3000L);
        return ResponseEntity.ok(new ApiSuccessResponseDto(result));
    }

    @RequestMapping("/httpCache")
    @Operation(summary = "httpCache", description = "httpCache 테스트", tags = {""})
    public ResponseEntity<ApiSuccessResponseDto<Long>> httpCache() {
        //todo : 현재 cache가 되지 않음, 이유확인이 필요함
        long cacheSec = 60L;
        CacheControl cacheControl = CacheControl.maxAge(cacheSec, TimeUnit.SECONDS).cachePublic().mustRevalidate();
        long result = System.currentTimeMillis();

        return ResponseEntity.ok().cacheControl(cacheControl).body(new ApiSuccessResponseDto(result));
    }

    @RequestMapping("/apiServiceError")
    @Operation(summary = "apiServiceError", description = "apiServiceError 테스트", tags = {""})
    public ResponseEntity<ApiSuccessResponseDto<Integer>> apiServiceError(@RequestParam("errorType") int errorType) {
        int result = apiTestService.raiseServiceError(errorType);
        return ResponseEntity.ok(new ApiSuccessResponseDto(result));
    }
}
