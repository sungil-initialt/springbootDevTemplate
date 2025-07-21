package com.sptek._frameworkWebCore._example.unit.deprecated;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sptek._frameworkWebCore._example.unit.database.DatabaseService;
import com.sptek._frameworkWebCore.annotation.Enable_ResponseOfApiCommonSuccess_At_RestController;
import com.sptek._frameworkWebCore.annotation.Enable_ResponseOfApiGlobalException_At_RestController;
import com.sptek._frameworkWebCore.annotation.TestAnnotation_At_All;
import com.sptek._frameworkWebCore.annotation.annotationCondition.HasAnnotationOnMain_At_Bean;
import com.sptek._frameworkWebCore.base.apiResponseDto.ApiCommonSuccessResponseDto;
import com.sptek._frameworkWebCore.commonObject.vo.ProjectInfoVo;
import com.sptek._frameworkWebCore.eventListener.publisher.CustomEventPublisher;
import com.sptek._frameworkWebCore.support.CloseableHttpClientSupport;
import com.sptek._frameworkWebCore.support.RestTemplateSupport;
import com.sptek._projectCommon.eventListener.custom.event.ExampleEvent;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Slf4j
@RequiredArgsConstructor
@HasAnnotationOnMain_At_Bean(TestAnnotation_At_All.class)
@RestController
@Enable_ResponseOfApiCommonSuccess_At_RestController
@Enable_ResponseOfApiGlobalException_At_RestController
//@EnableDetailLogFilter("aaa")
//v1, v2 경로로 모두 접근 가능, produces를 통해 MediaType을 정할수 있으며 Agent가 해당 타입을 보낼때만 응답함. (TODO : xml로 응답하는 기능도 추가하면 좋을듯)
//@RequestMapping(value = {"/api/v1/", "/api/v2/"}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
@RequestMapping(value = {"/api/"})
@Tag(name = "Deprecate 예정", description = "Deprecate 예정 APIs") // for swagger

public class DEPRECATED_ApiController {
    String fooResponseUrl = "https://worldtimeapi.org/api/timezone/Asia/Seoul"; //아무 의미없는 사이트로 단순히 rest 응답을 주는 테스트용 서버가 필요했음

    private final ProjectInfoVo projectInfoVo;
    private final CloseableHttpClient closeableHttpClient;
    private final CloseableHttpClientSupport closeableHttpClientSupport;
    private final RestTemplate restTemplate;
    private final RestTemplateSupport restTemplateSupport;
    private final ObjectMapper objectMapper;
    private final CustomEventPublisher customEventPublisher;
    private final DatabaseService databaseService;


    @GetMapping("/0/example/restTemplate")
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

    @GetMapping("/0/example/restTemplateSupport")
    //reqConfig와 pool이 이미 설정된 restTemplate Bean을 사용하는, 좀더 사용성을 편리하게 만든 restTemplateSupport 사용하는 req 요청
    public Object restTemplateSupport() {
        ResponseEntity<String> responseEntity = restTemplateSupport.requestGet(fooResponseUrl, null, null);
        return responseEntity.getBody();
    }

    @GetMapping("/0/example/exampleEvent")
    public Object exampleEvent() {
        customEventPublisher.publishEvent(ExampleEvent.builder().eventMessage("exampleEvent 도착!").extraField("추가정보").build());
        return "published exampleEvent ";
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



    @GetMapping("/0/example/httpCache")
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


}
