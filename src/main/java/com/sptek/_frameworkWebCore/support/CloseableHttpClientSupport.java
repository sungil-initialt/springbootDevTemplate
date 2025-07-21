package com.sptek._frameworkWebCore.support;

import com.sptek._frameworkWebCore.commonObject.dto.HttpClientResponseDto;
import com.sptek._frameworkWebCore.util.RequestUtil;
import com.sptek._frameworkWebCore.util.SptFwUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/*
closeableHttpClient을 쉽게 사용하기 위한 클레스로 Spring Bean 을 통해 주입받아 사용할 것
 */

@Slf4j
@RequiredArgsConstructor
public class CloseableHttpClientSupport {
    private final CloseableHttpClient closeableHttpClient;

    public HttpClientResponseDto requestGet(HttpMethod httpMethod, UriComponentsBuilder uriComponentsBuilder, @Nullable HttpHeaders httpHeaders, @Nullable Object requestBody) throws Exception {
        URI uri = uriComponentsBuilder.build(true).toUri();
        HttpUriRequest request = switch (httpMethod.toString()) {
            case "GET"    -> new HttpGet(uri);
            case "POST"   -> new HttpPost(uri);
            case "PUT"    -> new HttpPut(uri);
            case "DELETE" -> new HttpDelete(uri);
            default -> throw new IllegalArgumentException("Unsupported method: " + httpMethod);
        };

        if (!StringUtils.hasText(httpHeaders.getFirst(HttpHeaders.CONTENT_TYPE))) httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE); // CONTENT_TYPE 없을때 디폴트
        RequestUtil.applyRequestHeaders(request, httpHeaders);
        RequestUtil.applyRequestBody(request, requestBody);

        try (CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(request)) {
            HttpHeaders responseHeaders = new HttpHeaders();
            Arrays.stream(closeableHttpResponse.getHeaders())
                    .forEach(header -> responseHeaders.add(header.getName(), header.getValue()));
            String body = EntityUtils.toString(closeableHttpResponse.getEntity(), StandardCharsets.UTF_8);

            HttpClientResponseDto httpClientResponseDto = new HttpClientResponseDto(closeableHttpResponse.getCode(), responseHeaders, body);
            log.info(SptFwUtil.convertSystemNotice("Outbound", "HttpClient Response", httpClientResponseDto.toString()));
            return httpClientResponseDto;
        }
    }



    public static String DEPRECATED_convertResponseToString(HttpEntity httpEntity) throws Exception {
        String reponseString =EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
        log.debug("responseBody to String = {}", reponseString);

        EntityUtils.consume(httpEntity);
        return reponseString;
        
        //todo: response 결과를 라인별로 받아서 처리가 필요한 경우 사용 (코드 테스트 필요)
        /* 
        try (InputStreamReader inputStreamReader = new InputStreamReader(httpEntity.getContent(), StandardCharsets.UTF_8)) {
            String responseStr = new BufferedReader(inputStreamReader)
                    .lines()
                    .collect(Collectors.joining("\n"));

            if(httpEntity != null) {EntityUtils.consume(httpEntity);}
            return responseStr;
        }
         */
    }
}