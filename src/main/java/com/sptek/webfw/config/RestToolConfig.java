package com.sptek.webfw.config;

import com.sptek.webfw.support.CloseableHttpClientSupport;
import com.sptek.webfw.support.RestTemplateSupport;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Configuration
public class RestToolConfig {

    @Bean
    @DependsOn({"CloseableHttpClient"})
    @Autowired
    //CloseableHttpClient를 쉽게 쓸수있도록 기능 랩핑한 Bean
    public CloseableHttpClientSupport CloseableHttpClientSupport(CloseableHttpClient closeableHttpClient){
        CloseableHttpClientSupport myHttpClientSupport = new CloseableHttpClientSupport(closeableHttpClient);
        return myHttpClientSupport;
    }

    @Bean
    @DependsOn({"CloseableHttpClient"})
    @Autowired
    //reqConfig와 pool 관리를 내부적으로 하고 있는 RestTemplate을 @Autowired 해 사용할 수 있도록 Bean 구성함
    public RestTemplate RestTemplate(CloseableHttpClient closeableHttpClient){
        HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpComponentsClientHttpRequestFactory.setHttpClient(closeableHttpClient);
        RestTemplate restTemplate = new RestTemplate(httpComponentsClientHttpRequestFactory);

        return restTemplate;
    }

    @Bean
    @DependsOn({"RestTemplate"})
    @Autowired
    //restTemplate을 쉽게 쓸수있도록 기능 랩핑한 Bean
    public RestTemplateSupport RestTemplateSupport(RestTemplate restTemplate){
        RestTemplateSupport myRestTemplateSupport = new RestTemplateSupport(restTemplate);
        return myRestTemplateSupport;
    }



    private PoolingHttpClientConnectionManager getPoolingHttpClientConnectionManager() {
        int HTTP_CLIENT_MAX_CONN_TOTAL = 100;
        int HTTP_CLIENT_MAX_CONN_PER_ROUTE = 50;

        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
        poolingHttpClientConnectionManager.setMaxTotal(HTTP_CLIENT_MAX_CONN_TOTAL);
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(HTTP_CLIENT_MAX_CONN_PER_ROUTE);

        return poolingHttpClientConnectionManager;
    }

    private RequestConfig getRequestConfig(){
        int DEFAULT_CONNECT_TIMEOUT = 10 * 1000;
        int DEFAULT_CONNECTION_REQUEST_TIMEOUT = 10 * 1000; //connection pool 에서 커넥션을 얻어올때까지의 최대 시간

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.of(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS))
                .setConnectionRequestTimeout(Timeout.of(DEFAULT_CONNECTION_REQUEST_TIMEOUT,TimeUnit.MILLISECONDS))
                .build();

        return requestConfig;
    }

    @Bean
    //HttpClient를 사용하지 말고 CloseableHttpClient를 @Autowired 해 사용할 수 있도록 Bean 구성함
    public CloseableHttpClient CloseableHttpClient() {
        CloseableHttpClient closeableHttpClient = HttpClients.custom()
                .setConnectionManager(getPoolingHttpClientConnectionManager())
                .setDefaultRequestConfig(getRequestConfig())
                .build();

        return closeableHttpClient;
    }
}
