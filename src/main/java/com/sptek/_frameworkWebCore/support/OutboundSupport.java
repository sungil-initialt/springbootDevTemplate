package com.sptek._frameworkWebCore.support;

import com.sptek._frameworkWebCore.annotation.Enable_OutboundSupportLog_At_Main;
import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import com.sptek._frameworkWebCore.base.constant.MainClassAnnotationRegister;
import com.sptek._frameworkWebCore.commonObject.dto.HttpClientResponseDto;
import com.sptek._frameworkWebCore.util.RequestUtil;
import com.sptek._frameworkWebCore.util.SpringUtil;
import com.sptek._frameworkWebCore.util.SptFwUtil;
import com.sptek._frameworkWebCore.util.TypeConvertUtil;
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
import org.springframework.web.util.UriComponents;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
closeableHttpClient을 쉽게 사용하기 위한 클레스로 Spring Bean 을 통해 주입받아 사용할 것
 */

@Slf4j
@RequiredArgsConstructor
public class OutboundSupport {
    private final CloseableHttpClient closeableHttpClient;

    public HttpClientResponseDto request(HttpMethod httpMethod, UriComponents uriComponents) throws Exception {return request(httpMethod, uriComponents, null, null);}
    public HttpClientResponseDto request(HttpMethod httpMethod, UriComponents uriComponents, HttpHeaders httpHeaders) throws Exception {return request(httpMethod, uriComponents, httpHeaders, null);}
    public HttpClientResponseDto request(HttpMethod httpMethod, UriComponents uriComponents, Object requestBody) throws Exception {return request(httpMethod, uriComponents, null, requestBody);}
    public HttpClientResponseDto request(HttpMethod httpMethod, UriComponents uriComponents, @Nullable HttpHeaders httpHeaders, @Nullable Object requestBody) throws Exception {
        URI uri = uriComponents.encode().toUri();
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

            String outboundId = LocalDateTime.now().toString();
            String responseBodyStr = EntityUtils.toString(closeableHttpResponse.getEntity(), StandardCharsets.UTF_8);
            HttpClientResponseDto httpClientResponseDto = new HttpClientResponseDto(closeableHttpResponse.getCode(), responseHeaders, responseBodyStr);

            if (MainClassAnnotationRegister.hasAnnotation(Enable_OutboundSupportLog_At_Main.class)) {
                String requestBodyStr = "";
                if (requestBody != null) {
                    requestBodyStr = requestBody instanceof String ? String.valueOf(requestBody) : TypeConvertUtil.objectToJsonWithoutRootName(requestBody, false);
                }

                String logBody = String.format(
                        "outbound Id : %s\n"
                                + "-->(%s) url : %s\n"
                                + "requestHeader : %s\n"
                                + "requestBody : %s\n"
                                + "<--(%s)\n"
                                + "responseHeader : %s\n"
                                + "responseBody : %s\n"
                        , outboundId
                        , httpMethod.name(), uriComponents.toString()
                        , httpHeaders.toString()
                        , requestBodyStr
                        , httpClientResponseDto.code()
                        , httpClientResponseDto.headers().toString()
                        , httpClientResponseDto.body()
                );
                log.info(SptFwUtil.convertSystemNotice(uriComponents.getHost(), "OutboundSupport Information", logBody));

                try {
                    // 사용자 호출이 아닌 케이스 (스케줄러등) 에서 사용시 request를 얻지 못할 수 있다.
                    List<String> relatedOutbounds = (List<String>) SpringUtil.getRequest().getAttribute(CommonConstants.REQ_PROPERTY_FOR_LOGGING_RELATED_OUTBOUNDS);
                    if (relatedOutbounds == null) {
                        relatedOutbounds = new ArrayList<>();
                    }
                    relatedOutbounds.add(outboundId + " " + httpMethod.name() + " " + uriComponents.toString() + " --> " + httpClientResponseDto.code());
                    SpringUtil.getRequest().setAttribute(CommonConstants.REQ_PROPERTY_FOR_LOGGING_RELATED_OUTBOUNDS, relatedOutbounds);
                } catch (Exception e) {
                    log.debug("SpringUtil.getRequest() is null. Not logging related outbound information.");
                }
            }
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