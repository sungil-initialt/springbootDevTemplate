package com.sptek.webfw.support;


import lombok.Getter;
import lombok.Setter;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class RestTemplateSupport{

    private int DEFAULT_CONNECT_TIMEOUT = 10 * 1000;
    private int DEFAULT_CONNECTION_REQUEST_TIMEOUT = 10 * 1000;
    private int HTTP_CLIENT_MAX_CONN_TOTAL = 100;
    private int HTTP_CLIENT_MAX_CONN_PER_ROUTE = 20;
    PoolingHttpClientConnectionManager poolingHttpClientConnectionManager;
    private RestTemplate restTemplate;
    public @Getter @Setter RequestConfig requestConfig;

    public RestTemplateSupport(){
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
        CloseableHttpClient closeableHttpClient = HttpClients.custom()
                .setConnectionManager(poolingHttpClientConnectionManager)
                .setDefaultRequestConfig(requestConfig)
                .build();

        HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpComponentsClientHttpRequestFactory.setHttpClient(closeableHttpClient);
        restTemplate = new RestTemplate(httpComponentsClientHttpRequestFactory);
    }

    public ResponseEntity<String> requestGet(String requestUri, @Nullable LinkedMultiValueMap<String, String> queryParams, @Nullable HttpHeaders httpHeaders) {
        setCloseableHttpClient();

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(requestUri).queryParams(queryParams);
        String finalUrl = builder.toUriString();

        RequestEntity<Void> requestEntity = RequestEntity
                .method(HttpMethod.GET, finalUrl)
                .headers(httpHeaders)
                .build();

        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
        return responseEntity;
    }

    public ResponseEntity<String> requestPost(String requestUri, @Nullable LinkedMultiValueMap<String, String> queryParams, @Nullable HttpHeaders httpHeaders, @Nullable LinkedMultiValueMap<String, Object> requestBody) {
        setCloseableHttpClient();

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(requestUri).queryParams(queryParams);
        String finalUrl = builder.toUriString();

        RequestEntity<MultiValueMap<String, Object>> requestEntity = RequestEntity
                .post(finalUrl)
                .headers(httpHeaders)
                .body(requestBody);

        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
        return responseEntity;
    }

    public String convertResponseToString(ResponseEntity<String> responseEntity) throws IOException {
        String resultStr = responseEntity.getBody();
        return resultStr;
    }
}
