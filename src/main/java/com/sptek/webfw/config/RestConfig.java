package com.sptek.webfw.config;

import com.sptek.webfw.support.CloseableHttpClientSupport;
import com.sptek.webfw.support.RestTemplateSupport;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Configuration
public class RestConfig {
    private int DEFAULT_CONNECT_TIMEOUT = 10 * 1000;
    private int DEFAULT_CONNECTION_REQUEST_TIMEOUT = 10 * 1000;
    private int HTTP_CLIENT_MAX_CONN_TOTAL = 100;
    private int HTTP_CLIENT_MAX_CONN_PER_ROUTE = 20;
    private static final int IDLE_CONNECTION_TIMEOUT = 30 * 1000;

    @Bean
    public PoolingHttpClientConnectionManager getPoolingHttpClientConnectionManager() {
        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
        poolingHttpClientConnectionManager.setMaxTotal(HTTP_CLIENT_MAX_CONN_TOTAL);
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(HTTP_CLIENT_MAX_CONN_PER_ROUTE);

        return poolingHttpClientConnectionManager;
    }

    private RequestConfig getRequestConfig(){
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.of(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS))
                .setConnectionRequestTimeout(Timeout.of(DEFAULT_CONNECTION_REQUEST_TIMEOUT,TimeUnit.MILLISECONDS))
                .build();

        return requestConfig;
    }

    @Bean
    public CloseableHttpClient getCloseableHttpClient() {
        CloseableHttpClient closeableHttpClient = HttpClients.custom()
                .setConnectionManager(getPoolingHttpClientConnectionManager())
                .setDefaultRequestConfig(getRequestConfig())
                .build();

        return closeableHttpClient;
    }

    @Bean
    public CloseableHttpClientSupport getHttpClientSupport(){
        CloseableHttpClientSupport myHttpClientSupport = new CloseableHttpClientSupport();
        return myHttpClientSupport;
    }

    @Bean
    public RestTemplate getRestTemplate(){
        HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpComponentsClientHttpRequestFactory.setHttpClient(getCloseableHttpClient());
        RestTemplate restTemplate = new RestTemplate(httpComponentsClientHttpRequestFactory);

        return restTemplate;
    }

    @Bean
    public RestTemplateSupport getRestTemplateSupport(){
        RestTemplateSupport myRestTemplateSupport = new RestTemplateSupport();
        return myRestTemplateSupport;
    }

}
