package com.sptek._frameworkWebCore._example.unit.domain1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sptek._frameworkWebCore._example.dto.FileUploadDto;
import com.sptek._frameworkWebCore.annotation.EnableResponseOfApiCommonSuccess_InRestController;
import com.sptek._frameworkWebCore.annotation.EnableResponseOfApiGlobalException_InRestController;
import com.sptek._frameworkWebCore.annotation.TestAnnotation_InAll;
import com.sptek._frameworkWebCore.annotation.annotationCondition.HasAnnotationOnMain_InBean;
import com.sptek._frameworkWebCore.base.apiResponseDto.ApiCommonSuccessResponseDto;
import com.sptek._frameworkWebCore.eventListener.publisher.CustomEventPublisher;
import com.sptek._frameworkWebCore.globalVo.ProjectInfoVo;
import com.sptek._frameworkWebCore.support.CloseableHttpClientSupport;
import com.sptek._frameworkWebCore.support.RestTemplateSupport;
import com.sptek._frameworkWebCore.util.FileUtil;
import com.sptek._projectCommon.eventListener.custom.event.ExampleEvent;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Predicate;

@Slf4j
@RequiredArgsConstructor
@HasAnnotationOnMain_InBean(TestAnnotation_InAll.class)
@RestController
@EnableResponseOfApiCommonSuccess_InRestController
@EnableResponseOfApiGlobalException_InRestController
//@EnableDetailLogFilter("aaa")
//v1, v2 경로로 모두 접근 가능, produces를 통해 MediaType을 정할수 있으며 Agent가 해당 타입을 보낼때만 응답함. (TODO : xml로 응답하는 기능도 추가하면 좋을듯)
//@RequestMapping(value = {"/api/v1/", "/api/v2/"}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
@RequestMapping(value = {"/api/v1/domain1/"})
@Tag(name = "domain1", description = "테스트를 위한 기본 api 그룹") // for swagger

public class Domain1ApiController {
    String fooResponseUrl = "https://worldtimeapi.org/api/timezone/Asia/Seoul"; //아무 의미없는 사이트로 단순히 rest 응답을 주는 테스트용 서버가 필요했음

    private final ProjectInfoVo projectInfoVo;
    private final CloseableHttpClient closeableHttpClient;
    private final CloseableHttpClientSupport closeableHttpClientSupport;
    private final RestTemplate restTemplate;
    private final RestTemplateSupport restTemplateSupport;
    private final ObjectMapper objectMapper;
    private final CustomEventPublisher customEventPublisher;





    @GetMapping("/closeableHttpClient")
    //reqConfig와 pool이 이미 설정된 closeableHttpClient Bean을 사용하여 req 요청
    public Object closeableHttpClient() throws Exception{
        log.debug("closeableHttpClient identityHashCode : {}", System.identityHashCode(closeableHttpClient));

        HttpGet httpGet = new HttpGet(fooResponseUrl);
        httpGet.addHeader("X-test-id", "xyz");
        CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);

        HttpEntity httpEntity = closeableHttpResponse.getEntity();
        return EntityUtils.toString(httpEntity);
    }

    @GetMapping("/closeableHttpClientSupport")
    //reqConfig와 pool이 이미 설정된 closeableHttpClient Bean을 사용하는, 좀더 사용성을 편리하게 만든 closeableHttpClientSupport 사용하는 req 요청
    public Object closeableHttpClientSupport() throws Exception{
        log.debug("closeableHttpClientSupport identityHashCode : {}", System.identityHashCode(closeableHttpClientSupport));

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(fooResponseUrl);
        HttpEntity httpEntity = closeableHttpClientSupport.requestPost(uriComponentsBuilder.toUriString(), null, null);

        return CloseableHttpClientSupport.convertResponseToString(httpEntity);
    }

    @GetMapping("/restTemplate")
    //reqConfig와 pool이 이미 설정된 restTemplate Bean을 사용하여 req 요청
    public Object restTemplate() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(fooResponseUrl);
        String finalUrl = builder.toUriString();
        RequestEntity<Void> requestEntity = RequestEntity
                .method(HttpMethod.GET, finalUrl)
                .build();

        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
        return responseEntity.getBody();
    }

    @GetMapping("/restTemplateSupport")
    //reqConfig와 pool이 이미 설정된 restTemplate Bean을 사용하는, 좀더 사용성을 편리하게 만든 restTemplateSupport 사용하는 req 요청
    public Object restTemplateSupport() {
        ResponseEntity<String> responseEntity = restTemplateSupport.requestGet(fooResponseUrl, null, null);
        return responseEntity.getBody();
    }









    @PostMapping(value = "/fileUpload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiCommonSuccessResponseDto<List<FileUploadDto>>> fileUpload(@Value("${storage.localMultipartFilesBasePath}") String baseStoragePath
            , @RequestParam("uploadFiles") MultipartFile[] uploadFiles
            , @RequestParam("fileDescription") String fileDescription) throws Exception {

        log.debug("file count = {}, fileDescription = {}", uploadFiles.length, fileDescription);
        //todo: 실제 상황에서는 부가정보 저장 처리등 조치 필요

        String additionalPath = ""; //로그인계정번호등 필요한 구분 디렉토리가 있는다면 추가
        Predicate<MultipartFile> exceptionFilter = multipartFile -> multipartFile.getContentType().startsWith("image") ? false : true; //ex를 발생시키는 조건 (필요에 따라 수정)
        List<FileUploadDto> FileUploadDtos = FileUtil.saveMultipartFiles(uploadFiles, baseStoragePath, additionalPath, exceptionFilter);

        return ResponseEntity.ok(new ApiCommonSuccessResponseDto<>(FileUploadDtos));
    }

    @GetMapping(value = "/byteForImage")
    public ResponseEntity<byte[]> byteForImage(@Value("${storage.localMultipartFilesBasePath}") String baseStoragePath
            , @RequestParam("originFileName") String originFileName
            , @RequestParam("uuidForFileName") String uuidForFileName)  throws Exception {

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



//    @TestAnnotation_InAll
//    @EnableDetailLog_InMain_Controller_ControllerMethod("1111")
//    @PostMapping({"/httpCache", "/httpCache2"})
//    @Operation(summary = "httpCache", description = "httpCache 테스트", tags = {""})
//    public ResponseEntity<ApiCommonSuccessResponseDto<Long>> httpCachePost() {
//        log.debug("httpCache: post");
//        //todo : 현재 cache가 되지 않음, 이유 확인이 필요함
//        long cacheSec = 60L;
//        CacheControl cacheControl = CacheControl.maxAge(cacheSec, TimeUnit.SECONDS).cachePublic().mustRevalidate();
//        long result = System.currentTimeMillis();
//
//        return ResponseEntity.ok().cacheControl(cacheControl).body(new ApiCommonSuccessResponseDto<>(result));
//    }



    @GetMapping("/httpCache")
    public ResponseEntity<ApiCommonSuccessResponseDto<Long>> httpCacheGet(HttpServletResponse response, HttpServletRequest request) {
        log.debug("xxx");
        // 현재 시간 (밀리초)
        long currentTimeMillis = System.currentTimeMillis();

        // 현재 시간(GMT 형식으로 Last-Modified 용)
        Instant now = Instant.ofEpochMilli(currentTimeMillis);
        String currentTime = DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneOffset.UTC).format(now);

        // 1분(60초)을 지나기 위한 기준 시간 계산
        Instant oneMinuteAgo = now.minusSeconds(60);

        // 요청 헤더의 If-Modified-Since 값 가져오기
        String ifModifiedSince = request.getHeader(HttpHeaders.IF_MODIFIED_SINCE);
        log.debug("ifModifiedSince : {}", ifModifiedSince);

        if (ifModifiedSince != null) {
            try {
                // If-Modified-Since를 파싱하여 이전 요청 시간 계산
                Instant lastModifiedFromClient = Instant.from(
                        DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneOffset.UTC).parse(ifModifiedSince));

                // 클라이언트의 If-Modified-Since 확인, 1분 기준으로 캐싱 처리
                if (lastModifiedFromClient.isAfter(oneMinuteAgo)) {
                    // 1분이 지나지 않았다면 304 - Not Modified 처리
                    return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
                }
            } catch (Exception e) {
                // 잘못된 헤더 값이 들어올 경우 무시
            }
        }

        // 1분 이상이 지난 경우, 새 데이터를 내려주고 Last-Modified 갱신
        response.setHeader(HttpHeaders.LAST_MODIFIED, currentTime);
        //CacheControl cacheControl = CacheControl.maxAge(60, TimeUnit.SECONDS).cachePublic();//.mustRevalidate();
        // 200 OK로 응답
        return ResponseEntity.ok().body(new ApiCommonSuccessResponseDto<>(currentTimeMillis));
    }

    @GetMapping("/exampleEvent")
    public Object exampleEvent() {
        customEventPublisher.publishEvent(ExampleEvent.builder().eventMessage("exampleEvent 도착!").extraField("추가정보").build());
        return "published exampleEvent ";
    }
}
