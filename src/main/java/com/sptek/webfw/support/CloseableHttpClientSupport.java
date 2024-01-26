package com.sptek.webfw.support;

import com.sptek.webfw.util.TypeConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Slf4j
public class CloseableHttpClientSupport {
    private CloseableHttpClient closeableHttpClient;

    public CloseableHttpClientSupport(CloseableHttpClient closeableHttpClient){
        this.closeableHttpClient = closeableHttpClient;
    }

    public HttpEntity requestGet(String requestUrl, @Nullable HttpHeaders headers) throws Exception {
        HttpGet httpGet = new HttpGet(requestUrl);
        Optional.ofNullable(headers).ifPresent(h -> h.forEach((name, values) -> values.forEach(value -> httpGet.addHeader(name, value))));

        CloseableHttpResponse response = closeableHttpClient.execute(httpGet);
        return response.getEntity();

    }

    public HttpEntity requestPost(String requestUrl, @Nullable HttpHeaders headers, @Nullable Object requestBodyObject) throws IOException {
        return requestPost(requestUrl, headers, TypeConvertUtil.objectToJsonWithoutRootName(requestBodyObject, false));
    }

    public HttpEntity requestPost(String requestUrl, @Nullable HttpHeaders headers, @Nullable String requestBody) throws IOException {
        HttpPost httpPost = new HttpPost(requestUrl);
        Optional.ofNullable(headers).ifPresent(h -> h.forEach((name, values) -> values.forEach(value -> httpPost.addHeader(name, value))));

        //해더에 Content-Type이 없는 경우 default 로 json 타입으로 처리함
        if (httpPost.getFirstHeader(HttpHeaders.CONTENT_TYPE) == null) {
            httpPost.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        }

        if(StringUtils.isNotBlank(requestBody)) {
            StringEntity requestEntity = new StringEntity(requestBody);
            httpPost.setEntity(requestEntity);
        }

        log.info("closeableHttpClient identityHashCode in CloseableHttpClientSupport : {}", System.identityHashCode(closeableHttpClient));
        CloseableHttpResponse response = closeableHttpClient.execute(httpPost);
        return response.getEntity();
    }

    public HttpEntity requestPut(String requestUrl, @Nullable HttpHeaders headers, @Nullable Object requestBodyObject) throws IOException {
        return requestPut(requestUrl, headers, TypeConvertUtil.objectToJsonWithoutRootName(requestBodyObject, false));
    }

    public HttpEntity requestPut(String requestUrl, @Nullable HttpHeaders headers, @Nullable String requestBody) throws IOException {
        HttpPut httpPut = new HttpPut(requestUrl);
        Optional.ofNullable(headers).ifPresent(h -> h.forEach((name, values) -> values.forEach(value -> httpPut.addHeader(name, value))));

        if (httpPut.getFirstHeader(HttpHeaders.CONTENT_TYPE) == null) {
            httpPut.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        }

        if(StringUtils.isNotBlank(requestBody)) {
            StringEntity requestEntity = new StringEntity(requestBody);
            httpPut.setEntity(requestEntity);
        }

        CloseableHttpResponse response = closeableHttpClient.execute(httpPut);
        return response.getEntity();
    }

    public HttpEntity requestDelete(String requestUrl, @Nullable HttpHeaders headers) throws IOException {
        HttpDelete httpDelete = new HttpDelete(requestUrl);
        Optional.ofNullable(headers).ifPresent(h -> h.forEach((name, values) -> values.forEach(value -> httpDelete.addHeader(name, value))));

        CloseableHttpResponse response = closeableHttpClient.execute(httpDelete);
        return response.getEntity();
    }

    public static String convertResponseToString(HttpEntity httpEntity) throws Exception {

        String reponseString =EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
        if(httpEntity != null) {EntityUtils.consume(httpEntity);}
        return reponseString;
        
        //todo: response 결과를 라인별로 받아서 처리가 필요한 경우데 대한 코드로 확인 필요
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