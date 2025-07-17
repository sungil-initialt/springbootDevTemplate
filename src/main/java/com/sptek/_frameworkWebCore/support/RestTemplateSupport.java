package com.sptek._frameworkWebCore.support;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;

/*
RestTemplate을 쉽게 사용하기 위한 클레스로 Spring Bean 을 통해 주입받아 사용할 것
 */
@Slf4j
@RequiredArgsConstructor
public class RestTemplateSupport{

    private final RestTemplate restTemplate;

    public ResponseEntity<String> requestGet(String requestUri, @Nullable LinkedMultiValueMap<String, String> queryParams, @Nullable HttpHeaders httpHeaders) {
        log.debug("requestUri = ({}), queryParams = ({}), httpHeaders = ({})", requestUri, queryParams, httpHeaders);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(requestUri);
        if (queryParams != null) {
            builder.queryParams(queryParams);
        }
        String finalUrl = builder.toUriString();

        RequestEntity<Void> requestEntity = RequestEntity
                .method(HttpMethod.GET, finalUrl)
                .headers(httpHeaders != null ? httpHeaders : new HttpHeaders())
                .build();
        try {
            return restTemplate.exchange(requestEntity, String.class);
        } catch (RestClientException e) {
            log.error("POST 요청 실패: {}", e.getMessage(), e);
            throw e;
        }
    }

    public ResponseEntity<String> requestPost(String requestUri, @Nullable LinkedMultiValueMap<String, String> queryParams, @Nullable HttpHeaders httpHeaders, @Nullable Object requestBody) {
        log.debug("requestUri = ({}), queryParams = ({}), httpHeaders = ({}), requestBody = ({})", requestUri, queryParams, httpHeaders, requestBody);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(requestUri);
        if (queryParams != null) {
            builder.queryParams(queryParams);
        }
        String finalUrl = builder.toUriString();
        HttpHeaders headers = httpHeaders != null ? new HttpHeaders(httpHeaders) : new HttpHeaders();
        if (!headers.containsKey(HttpHeaders.CONTENT_TYPE)) {
            log.warn("Content-Type header is missing. Setting default to APPLICATION_JSON");
            headers.setContentType(MediaType.APPLICATION_JSON);
        }

        RequestEntity<Object> requestEntity = RequestEntity
                .post(finalUrl)
                .headers(httpHeaders)
                .body(requestBody != null ? requestBody : new HashMap<>());
        try {
            return restTemplate.exchange(requestEntity, String.class);
        } catch (RestClientException e) {
            log.error("POST 요청 실패: {}", e.getMessage(), e);
            throw e;
        }
    }

    public String convertResponseToString(ResponseEntity<String> responseEntity) {
        String responseString = responseEntity.getBody();
        log.debug("responseBody to String = {}", responseString);
        return responseString;
    }
}
