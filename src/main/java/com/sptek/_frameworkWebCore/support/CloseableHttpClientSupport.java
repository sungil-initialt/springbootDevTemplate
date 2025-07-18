package com.sptek._frameworkWebCore.support;

import com.sptek._frameworkWebCore.util.TypeConvertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/*
closeableHttpClient을 쉽게 사용하기 위한 클레스로 Spring Bean 을 통해 주입받아 사용할 것
 */

@Slf4j
@RequiredArgsConstructor
public class CloseableHttpClientSupport {
    //todo: CloseableHttpClient 의 close 처리와 PoolingHttpClientConnectionManager shutdown 처리에 대해서 더 고민 필요함 (pool 모니터링 기능 필요)
    private final CloseableHttpClient closeableHttpClient;

    public HttpEntity requestGet(String requestUrl, @Nullable HttpHeaders headers) throws Exception {
        log.debug("requestUrl = ({}), headers = ({})", requestUrl, headers);

        HttpGet httpGet = new HttpGet(requestUrl);
        //해더 타입 불일치로 httpGet.addHeader(headers) 로 넣을 수 없음 (일일히 넣어 줌)
        Optional.ofNullable(headers).ifPresent(h -> h.forEach((name, values) -> values.forEach(value -> httpGet.addHeader(name, value))));

        try (CloseableHttpResponse response = closeableHttpClient.execute(httpGet)) {
            return response.getEntity();
        }
    }

    public HttpEntity requestPost(String requestUrl, @Nullable HttpHeaders headers, @Nullable Object requestBodyObject) throws IOException {
        return requestPost(requestUrl, headers, TypeConvertUtil.objectToJsonWithoutRootName(requestBodyObject, false));
    }

    public HttpEntity requestPost(String requestUrl, @Nullable HttpHeaders headers, @Nullable String requestBodyString) throws IOException {
        log.debug("requestUrl = ({}), headers = ({}), requestBody = ({})", requestUrl, headers, requestBodyString);

        HttpPost httpPost = new HttpPost(requestUrl);
        Optional.ofNullable(headers).ifPresent(h -> h.forEach((name, values) -> values.forEach(value -> httpPost.addHeader(name, value))));

        //해더에 Content-Type이 없는 경우 default 로 json 타입으로 처리함
        if (httpPost.getFirstHeader(HttpHeaders.CONTENT_TYPE) == null) {
            httpPost.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        }

        if(StringUtils.hasText(requestBodyString)) {
            StringEntity requestEntity = new StringEntity(requestBodyString);
            httpPost.setEntity(requestEntity);
        }

        try (CloseableHttpResponse response = closeableHttpClient.execute(httpPost)) {
            return response.getEntity();
        }
    }

    public HttpEntity requestPut(String requestUrl, @Nullable HttpHeaders headers, @Nullable Object requestBodyObject) throws IOException {
        return requestPut(requestUrl, headers, TypeConvertUtil.objectToJsonWithoutRootName(requestBodyObject, false));
    }

    public HttpEntity requestPut(String requestUrl, @Nullable HttpHeaders headers, @Nullable String requestBodyString) throws IOException {
        log.debug("requestUrl = ({}), headers = ({}), requestBody = ({})", requestUrl, headers, requestBodyString);

        HttpPut httpPut = new HttpPut(requestUrl);
        Optional.ofNullable(headers).ifPresent(h -> h.forEach((name, values) -> values.forEach(value -> httpPut.addHeader(name, value))));

        if (httpPut.getFirstHeader(HttpHeaders.CONTENT_TYPE) == null) {
            httpPut.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        }

        if(StringUtils.hasText(requestBodyString)) {
            StringEntity requestEntity = new StringEntity(requestBodyString);
            httpPut.setEntity(requestEntity);
        }

        try (CloseableHttpResponse response = closeableHttpClient.execute(httpPut)) {
            return response.getEntity();
        }
    }

    public HttpEntity requestDelete(String requestUrl, @Nullable HttpHeaders headers) throws IOException {
        log.debug("requestUrl = ({}), headers = ({})", requestUrl, headers);

        HttpDelete httpDelete = new HttpDelete(requestUrl);
        Optional.ofNullable(headers).ifPresent(h -> h.forEach((name, values) -> values.forEach(value -> httpDelete.addHeader(name, value))));

        try (CloseableHttpResponse response = closeableHttpClient.execute(httpDelete)) {
            return response.getEntity();
        }
    }

    public static String convertResponseToString(HttpEntity httpEntity) throws Exception {
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