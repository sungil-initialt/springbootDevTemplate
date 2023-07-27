package com.sptek.webfw.support;

import com.sptek.webfw.util.TypeConvertUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.util.Timeout;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CloseableHttpClientSupport {

    private int DEFAULT_CONNECT_TIMEOUT = 10 * 1000;
    private int DEFAULT_CONNECTION_REQUEST_TIMEOUT = 10 * 1000;
    private int HTTP_CLIENT_MAX_CONN_TOTAL = 3;
    private int HTTP_CLIENT_MAX_CONN_PER_ROUTE = 2;
    PoolingHttpClientConnectionManager poolingHttpClientConnectionManager;
    private CloseableHttpClient closeableHttpClient;
    public @Getter @Setter RequestConfig requestConfig;

    public CloseableHttpClientSupport(){
        poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
        poolingHttpClientConnectionManager.setMaxTotal(HTTP_CLIENT_MAX_CONN_TOTAL);
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(HTTP_CLIENT_MAX_CONN_PER_ROUTE);

        setTimeout(DEFAULT_CONNECT_TIMEOUT, DEFAULT_CONNECTION_REQUEST_TIMEOUT);
    }

    public void setTimeout(int connectTimeout, int connectionRequestTimeout){
        requestConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.of(connectTimeout, TimeUnit.MILLISECONDS))
                .setConnectionRequestTimeout(Timeout.of(connectionRequestTimeout,TimeUnit.MILLISECONDS))
                .build();
    }

    private void setCloseableHttpClient(){
        closeableHttpClient = HttpClients.custom()
                .setConnectionManager(poolingHttpClientConnectionManager)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    public HttpEntity requestGet(String requestUrl, @Nullable HttpHeaders headers) throws Exception {
        setCloseableHttpClient();

        HttpGet httpGet = new HttpGet(requestUrl);
        Optional.ofNullable(headers).ifPresent(h -> h.forEach((name, values) -> values.forEach(value -> httpGet.addHeader(name, value))));

        CloseableHttpResponse response = closeableHttpClient.execute(httpGet);
        return response.getEntity();

    }

    public HttpEntity requestPost(String requestUrl, @Nullable HttpHeaders headers, @Nullable Object requestBodyObject) throws IOException {
        return requestPost(requestUrl, headers, TypeConvertUtil.objectToJsonWithoutRootName(requestBodyObject, false));
    }

    public HttpEntity requestPost(String requestUrl, @Nullable HttpHeaders headers, @Nullable String requestBodyJson) throws IOException {
        setCloseableHttpClient();

        HttpPost httpPost = new HttpPost(requestUrl);
        httpPost.setHeader("Content-type", "application/json");
        Optional.ofNullable(headers).ifPresent(h -> h.forEach((name, values) -> values.forEach(value -> httpPost.addHeader(name, value))));

        if(StringUtils.isNotBlank(requestBodyJson)) {
            StringEntity requestEntity = new StringEntity(requestBodyJson);
            httpPost.setEntity(requestEntity);
        }

        CloseableHttpResponse response = closeableHttpClient.execute(httpPost);
        return response.getEntity();
    }

    public HttpEntity requestPut(String requestUrl, @Nullable HttpHeaders headers, @Nullable Object requestBodyObject) throws IOException {
        String bodyStr = "";
        if(requestBodyObject != null) bodyStr = TypeConvertUtil.objectToJsonWithoutRootName(requestBodyObject, false);

        return requestPut(requestUrl, headers, bodyStr);
    }

    public HttpEntity requestPut(String requestUrl, @Nullable HttpHeaders headers, @Nullable String requestBodyJson) throws IOException {
        setCloseableHttpClient();

        HttpPut httpPut = new HttpPut(requestUrl);
        httpPut.setHeader("Content-type", "application/json");
        Optional.ofNullable(headers).ifPresent(h -> h.forEach((name, values) -> values.forEach(value -> httpPut.addHeader(name, value))));

        if(StringUtils.isNotBlank(requestBodyJson)) {
            StringEntity requestEntity = new StringEntity(requestBodyJson);
            httpPut.setEntity(requestEntity);
        }

        CloseableHttpResponse response = closeableHttpClient.execute(httpPut);
        return response.getEntity();
    }

    public HttpEntity requestDelete(String requestUrl, @Nullable HttpHeaders headers) throws IOException {
        setCloseableHttpClient();

        HttpDelete httpDelete = new HttpDelete(requestUrl);
        Optional.ofNullable(headers).ifPresent(h -> h.forEach((name, values) -> values.forEach(value -> httpDelete.addHeader(name, value))));

        CloseableHttpResponse response = closeableHttpClient.execute(httpDelete);
        return response.getEntity();
    }

    public static String convertResponseToString(HttpEntity httpEntity) throws Exception {
        return EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
        /*
        try (InputStreamReader inputStreamReader = new InputStreamReader(httpEntity.getContent(), StandardCharsets.UTF_8)) {
            String responseStr = new BufferedReader(inputStreamReader)
                    .lines()
                    .collect(Collectors.joining("\n"));

            return responseStr;
        }
         */
    }

}