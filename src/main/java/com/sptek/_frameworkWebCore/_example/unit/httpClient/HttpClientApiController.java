package com.sptek._frameworkWebCore._example.unit.httpClient;

import com.sptek._frameworkWebCore.annotation.Enable_ResponseOfApiCommonSuccess_At_RestController;
import com.sptek._frameworkWebCore.annotation.Enable_ResponseOfApiGlobalException_At_RestController;
import com.sptek._frameworkWebCore.commonObject.dto.HttpClientResponseDto;
import com.sptek._frameworkWebCore.support.CloseableHttpClientSupport;
import com.sptek._frameworkWebCore.util.TypeConvertUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@Enable_ResponseOfApiCommonSuccess_At_RestController
@Enable_ResponseOfApiGlobalException_At_RestController
@RequestMapping(value = {"/api/"}, produces = {MediaType.APPLICATION_JSON_VALUE/*, MediaType.APPLICATION_XML_VALUE*/})
@Tag(name = "HttpClient (Pooling connection Manager)", description = "")

public class HttpClientApiController {
    private final CloseableHttpClientSupport closeableHttpClientSupport;
    String apiTestUrl = "https://jsonplaceholder.typicode.com";
    MyTestDto apiTestDto = new MyTestDto(0, "my title!", "my content!", null);

    @GetMapping("/01/example/httpClient/closeableHttpClientGet")
    @Operation(summary = "01. GET without Pool", description = "")
    public Object closeableHttpClientGet() throws Exception {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(apiTestUrl);
        uriComponentsBuilder.path("posts/{id}").buildAndExpand(Map.of("id", 1));
        uriComponentsBuilder.queryParam("myKey", "myValue");

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpGet request = new HttpGet(uriComponentsBuilder.build(true).toUri());
            request.addHeader("X-TEST_KEY", "X-TEST_VALUE");
            try (CloseableHttpResponse closeableHttpResponse = httpClient.execute(request)) {
                return closeableHttpResponse.getCode();
            }
        }
    }

    @GetMapping("/02/example/httpClient/closeableHttpClientPost")
    @Operation(summary = "02. POST without Pool", description = "")
    public Object closeableHttpClientPost() throws Exception {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(apiTestUrl);
        uriComponentsBuilder.path("posts");
        uriComponentsBuilder.queryParam("myKey", "myValue");


        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpPost request = new HttpPost(uriComponentsBuilder.build(true).toUri());
            request.addHeader("X-TEST_KEY", "X-TEST_VALUE");
            request.setEntity(new StringEntity(
                    // object -> json
                    TypeConvertUtil.objectToJsonWithoutRootName(apiTestDto, false)
                    , ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse closeableHttpResponse = httpClient.execute(request)) {
                if (closeableHttpResponse.getCode() < 200 || closeableHttpResponse.getCode() >= 300) return "http client request failed. (result code: " + closeableHttpResponse.getCode() + ")";
                return TypeConvertUtil.jsonToClass(EntityUtils.toString(closeableHttpResponse.getEntity(), StandardCharsets.UTF_8), MyTestDto.class);
            }
        }
    }

    @GetMapping("/03/example/httpClient/closeableHttpClientSupportGet")
    @Operation(summary = "03. GET with Pool", description = "")
    public Object closeableHttpClientSupportGet() throws Exception {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(apiTestUrl);
        uriComponentsBuilder.path("posts/{id}").buildAndExpand(Map.of("id", 1));
        uriComponentsBuilder.queryParam("myKey", "myValue");
        HttpHeaders httpHeaders = TypeConvertUtil.objMapToHttpHeaders(Map.of("X-TEST_KEY", "X-TEST_VALUE"));

        HttpClientResponseDto httpClientResponseDto = closeableHttpClientSupport.requestGet(uriComponentsBuilder, httpHeaders);
        return httpClientResponseDto.code();
    }

    @GetMapping("/04/example/httpClient/closeableHttpClientSupportPost")
    @Operation(summary = "04. POST with Pool", description = "")
    public Object closeableHttpClientSupportPost() throws Exception {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(apiTestUrl);
        uriComponentsBuilder.path("posts");
        uriComponentsBuilder.queryParam("myKey", "myValue");
        HttpHeaders httpHeaders = TypeConvertUtil.objMapToHttpHeaders(Map.of("X-TEST_KEY", "X-TEST_VALUE"));

        HttpClientResponseDto httpClientResponseDto = closeableHttpClientSupport.requestPost(uriComponentsBuilder, httpHeaders, apiTestDto);
        if (httpClientResponseDto.code() < 200 || httpClientResponseDto.code() >= 300)  return "http client request failed. (result code: " + httpClientResponseDto.code() + ")";
        return TypeConvertUtil.jsonToClass(httpClientResponseDto.body(), MyTestDto.class);
    }

    @GetMapping("/05/example/httpClient/closeableHttpClientSupportPut")
    @Operation(summary = "05. PUT with Pool", description = "")
    public Object closeableHttpClientSupportPut() throws Exception {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(apiTestUrl);
        uriComponentsBuilder.path("posts/{id}").buildAndExpand(Map.of("id", 1));
        uriComponentsBuilder.queryParam("myKey", "myValue");
        HttpHeaders httpHeaders = TypeConvertUtil.objMapToHttpHeaders(Map.of("X-TEST_KEY", "X-TEST_VALUE"));

        HttpClientResponseDto httpClientResponseDto = closeableHttpClientSupport.requestPut(uriComponentsBuilder, httpHeaders, apiTestDto);
        if (httpClientResponseDto.code() < 200 || httpClientResponseDto.code() >= 300) return "http client request failed. (result code: " + httpClientResponseDto.code() + ")";
        return TypeConvertUtil.jsonToClass(httpClientResponseDto.body(), MyTestDto.class);
    }

    @GetMapping("/06/example/httpClient/closeableHttpClientSupportDelete")
    @Operation(summary = "06. DELETE with Pool", description = "")
    public Object closeableHttpClientSupportDelete() throws Exception {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(apiTestUrl);
        uriComponentsBuilder.path("posts/{id}").buildAndExpand(Map.of("id", 1));
        uriComponentsBuilder.queryParam("myKey", "myValue");
        HttpHeaders httpHeaders = TypeConvertUtil.objMapToHttpHeaders(Map.of("X-TEST_KEY", "X-TEST_VALUE"));

        HttpClientResponseDto httpClientResponseDto = closeableHttpClientSupport.requestDelete(uriComponentsBuilder, httpHeaders);
        if (httpClientResponseDto.code() < 200 || httpClientResponseDto.code() >= 300) return "http client request failed. (result code: " + httpClientResponseDto.code() + ")";
        return TypeConvertUtil.jsonToClass(httpClientResponseDto.body(), MyTestDto.class);
    }

    // api Test Dto
    private record MyTestDto(int id, String title, String content, String extraField) {}
}


