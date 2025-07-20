package com.sptek._frameworkWebCore._example.unit.httpClient;

import com.sptek._frameworkWebCore.annotation.Enable_ResponseOfApiCommonSuccess_At_RestController;
import com.sptek._frameworkWebCore.annotation.Enable_ResponseOfApiGlobalException_At_RestController;
import com.sptek._frameworkWebCore.commonObject.dto.HttpClientResponseDto;
import com.sptek._frameworkWebCore.support.CloseableHttpClientSupport;
import com.sptek._frameworkWebCore.util.TypeConvertUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Slf4j
@RestController
@RequiredArgsConstructor
@Enable_ResponseOfApiCommonSuccess_At_RestController
@Enable_ResponseOfApiGlobalException_At_RestController
@RequestMapping(value = {"/api/"}, produces = {MediaType.APPLICATION_JSON_VALUE/*, MediaType.APPLICATION_XML_VALUE*/})
@Tag(name = "HttpClient (Pooling connection Manager)", description = "")

public class HttpClientApiController {
    private final CloseableHttpClientSupport closeableHttpClientSupport;

    String apiTestUrl = "https://jsonplaceholder.typicode.com/posts";
    RequestTestDto requestTestDto = new RequestTestDto(0, "my title!", "my content!");

    @GetMapping("/01/example/httpClient/closeableHttpClientWithoutPoolingConnectionManager")
    @Operation(summary = "01. closeableHttpClient 단독 사용 ", description = "")
    public Object closeableHttpClientWithoutPoolingConnectionManager() throws Exception {
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpPost httpPost = new HttpPost(apiTestUrl);

            // JSON 바디 설정
            StringEntity requestEntity = new StringEntity(TypeConvertUtil.objectToJsonWithoutRootName(requestTestDto, false), ContentType.APPLICATION_JSON);
            httpPost.setEntity(requestEntity);

            // 요청 실행
            try (CloseableHttpResponse closeableHttpResponse = httpClient.execute(httpPost)) {
                if (closeableHttpResponse.getCode() < 200 || closeableHttpResponse.getCode() >= 300) {
                    return "http client request failed. (result code: " + closeableHttpResponse.getCode() + ")";
                }
                return TypeConvertUtil.jsonToClass(EntityUtils.toString(closeableHttpResponse.getEntity(), StandardCharsets.UTF_8), RequestTestDto.class);
            }
        }
    }

    @GetMapping("/02/example/httpClient/closeableHttpClientSupport")
    @Operation(summary = "01. closeableHttpClientSupport with PoolingConnectionManager  ", description = "")
    public Object closeableHttpClientSupport() throws Exception {
        HttpClientResponseDto httpClientResponseDto = closeableHttpClientSupport.requestPost(apiTestUrl, null, requestTestDto);
        if (httpClientResponseDto.code() < 200 || httpClientResponseDto.code() >= 300) {
            return "http client request failed. (result code: " + httpClientResponseDto.code() + ")";
        }
        return TypeConvertUtil.jsonToClass(httpClientResponseDto.body(), RequestTestDto.class);
    }



    // Test Dto
    private record RequestTestDto(int id, String title, String content) {}
}


