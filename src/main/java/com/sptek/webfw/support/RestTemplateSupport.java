package com.sptek.webfw.support;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

/*
RestTemplate을 쉽게 사용하기 위한 클레스로 직접 생성(new) 하지 않고 @Autowired로 주입받아 사용해야 한다.
 */
@Slf4j
public class RestTemplateSupport{

    private RestTemplate restTemplate;

    public RestTemplateSupport(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<String> requestGet(String requestUri, @Nullable LinkedMultiValueMap<String, String> queryParams, @Nullable HttpHeaders httpHeaders) {
        log.debug("requestUri = ({}), queryParams = ({}), httpHeaders = ({})", requestUri, queryParams, httpHeaders);

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
        log.debug("requestUri = ({}), queryParams = ({}), httpHeaders = ({}), requestBody = ({})", requestUri, queryParams, httpHeaders, requestBody);

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
        String reponseString = responseEntity.getBody();
        log.debug("responseBody to String = {}", reponseString);

        return reponseString;
    }
}
