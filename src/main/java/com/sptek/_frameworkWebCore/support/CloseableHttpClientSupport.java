package com.sptek._frameworkWebCore.support;

import com.sptek._frameworkWebCore.commonObject.dto.HttpClientResponseDto;
import com.sptek._frameworkWebCore.util.TypeConvertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;

/*
closeableHttpClient을 쉽게 사용하기 위한 클레스로 Spring Bean 을 통해 주입받아 사용할 것
 */

@Slf4j
@RequiredArgsConstructor
public class CloseableHttpClientSupport {
    private final CloseableHttpClient closeableHttpClient;

    public HttpClientResponseDto requestGet(String requestUrl, @Nullable HttpHeaders headers) throws Exception {
        log.debug("requestUrl = ({}), headers = ({})", requestUrl, headers);

        HttpGet httpGet = new HttpGet(requestUrl);
        //해더 타입 불일치로 httpGet.addHeader(headers) 로 넣을 수 없음 (일일히 넣어 줌)
        Optional.ofNullable(headers).ifPresent(h -> h.forEach((name, values) -> values.forEach(value -> httpGet.addHeader(name, value))));

        try (CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet)) {
            HttpHeaders responseHeaders = new HttpHeaders();
            Arrays.stream(closeableHttpResponse.getHeaders())
                    .forEach(header -> responseHeaders.add(header.getName(), header.getValue()));
            return new HttpClientResponseDto(closeableHttpResponse.getCode(), responseHeaders, EntityUtils.toString(closeableHttpResponse.getEntity(), StandardCharsets.UTF_8));
        }
    }

    public HttpClientResponseDto requestPost(String requestUrl, @Nullable HttpHeaders headers, @Nullable Object requestBodyObject) throws IOException, ParseException {
        return requestPost(requestUrl, headers, TypeConvertUtil.objectToJsonWithoutRootName(requestBodyObject, false));
    }
    public HttpClientResponseDto requestPost(String requestUrl, @Nullable HttpHeaders headers, @Nullable String requestBodyString) throws IOException, ParseException {
        log.debug("requestUrl = ({}), headers = ({}), requestBody = ({})", requestUrl, headers, requestBodyString);

        HttpPost httpPost = new HttpPost(requestUrl);
        Optional.ofNullable(headers).ifPresent(h -> h.forEach((name, values) -> values.forEach(value -> httpPost.addHeader(name, value))));

        if (httpPost.getFirstHeader(HttpHeaders.CONTENT_TYPE) == null) {
            httpPost.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        }
        if (StringUtils.hasText(requestBodyString)) {
            httpPost.setEntity(new StringEntity(requestBodyString));
        }
        try (CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpPost)) {
            HttpHeaders responseHeaders = new HttpHeaders();
            Arrays.stream(closeableHttpResponse.getHeaders())
                    .forEach(header -> responseHeaders.add(header.getName(), header.getValue()));
            return new HttpClientResponseDto(closeableHttpResponse.getCode(), responseHeaders, EntityUtils.toString(closeableHttpResponse.getEntity(), StandardCharsets.UTF_8));
        }
    }

    public HttpClientResponseDto requestPut(String requestUrl, @Nullable HttpHeaders headers, @Nullable Object requestBodyObject) throws IOException, ParseException {
        return requestPut(requestUrl, headers, TypeConvertUtil.objectToJsonWithoutRootName(requestBodyObject, false));
    }
    public HttpClientResponseDto requestPut(String requestUrl, @Nullable HttpHeaders headers, @Nullable String requestBodyString) throws IOException, ParseException {
        log.debug("requestUrl = ({}), headers = ({}), requestBody = ({})", requestUrl, headers, requestBodyString);

        HttpPut httpPut = new HttpPut(requestUrl);
        Optional.ofNullable(headers).ifPresent(h -> h.forEach((name, values) -> values.forEach(value -> httpPut.addHeader(name, value))));

        if (httpPut.getFirstHeader(HttpHeaders.CONTENT_TYPE) == null) {
            httpPut.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        }
        if(StringUtils.hasText(requestBodyString)) {
            httpPut.setEntity(new StringEntity(requestBodyString));
        }
        try (CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpPut)) {
            HttpHeaders responseHeaders = new HttpHeaders();
            Arrays.stream(closeableHttpResponse.getHeaders())
                    .forEach(header -> responseHeaders.add(header.getName(), header.getValue()));
            return new HttpClientResponseDto(closeableHttpResponse.getCode(), responseHeaders, EntityUtils.toString(closeableHttpResponse.getEntity(), StandardCharsets.UTF_8));
        }
    }

    public HttpClientResponseDto requestDelete(String requestUrl, @Nullable HttpHeaders headers) throws Exception {
        log.debug("requestUrl = ({}), headers = ({})", requestUrl, headers);

        HttpDelete httpDelete = new HttpDelete(requestUrl);
        Optional.ofNullable(headers).ifPresent(h -> h.forEach((name, values) -> values.forEach(value -> httpDelete.addHeader(name, value))));

        try (CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpDelete)) {
            HttpHeaders responseHeaders = new HttpHeaders();
            Arrays.stream(closeableHttpResponse.getHeaders())
                    .forEach(header -> responseHeaders.add(header.getName(), header.getValue()));
            return new HttpClientResponseDto(closeableHttpResponse.getCode(), responseHeaders, EntityUtils.toString(closeableHttpResponse.getEntity(), StandardCharsets.UTF_8));
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