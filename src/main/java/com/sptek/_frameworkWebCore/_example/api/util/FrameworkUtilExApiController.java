package com.sptek._frameworkWebCore._example.api.util;

import com.sptek._frameworkWebCore.annotation.EnableResponseOfApiCommonSuccess_InRestController;
import com.sptek._frameworkWebCore.annotation.EnableResponseOfApiGlobalException_InRestController;
import com.sptek._frameworkWebCore.util.RequestUtil;
import com.sptek._frameworkWebCore.util.TypeConvertUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@EnableResponseOfApiCommonSuccess_InRestController
@EnableResponseOfApiGlobalException_InRestController
@RequestMapping(value = {"/api/v1/example/"}, produces = {MediaType.APPLICATION_JSON_VALUE/*, MediaType.APPLICATION_XML_VALUE*/}) // 클라이언트가 Accept 해더를 보낼 경우 제공하는 미디어 타입이 일치해야함(없으면 406)
@Tag(name = "Framework 공통 유틸 사용 예시", description = "")

public class FrameworkUtilExApiController {
    @GetMapping("/testRequestUtil")
    @Operation(summary = "request 요청과 관련해 URL, reqIP, Header, parameter를 제공", description = "", tags = {""})
    public Object reqResUtil(HttpServletRequest request) {
        String reqFullUrl = RequestUtil.getRequestUrlQuery(request);
        String reqIp = RequestUtil.getReqUserIp(request);
        String headers = RequestUtil.getRequestHeaderMap(request).toString();
        String params = TypeConvertUtil.strArrMapToString(RequestUtil.getRequestParameterMap(request));

        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("reqFullUrl", reqFullUrl);
        resultMap.put("reqIp", reqIp);
        resultMap.put("headers", headers);
        resultMap.put("params", params);

        return resultMap;
    }
}
