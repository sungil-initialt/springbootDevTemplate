package com.sptek.webfw.config.filter;

/*
Xss 방지 필터 목적인데.. request param 으로 들어오는 값들에 대한 처리는 필터에서 적용하기가 애매함 (해당 코드 request body 에만 Xss 필터가 적용됨)
objectMapper 셋팅에서 XssProtectSupport 클레스를 적용하는 방식으로 처리하여 지금은 우선 주석 처리함
컨트롤러 이전단계에서(필터등) request의 stream을 읽어버리면 컨틀롤러에서는 비어있는 request가 넘어가기 때문에 컨트롤러 이전에 request 를 읽은 경우 아래 코드를 참조하도록 남김
*/

/*
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sptek.webfw.support.HttpServletRequestWrapperSupport;
import com.sptek.webfw.util.SecureUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Order(1)
public class XssProtectFilter extends OncePerRequestFilter {

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        HttpServletRequestWrapperSupport wrappedRequest = new HttpServletRequestWrapperSupport(request);
        String reqBody = IOUtils.toString(wrappedRequest.getReader()); //컨트럴러 이전 단계에서 Request 스트림이 읽어졌기 때문에 대체 request를 생성해서 넘겨줘야 함

        if (!StringUtils.isEmpty(reqBody)) {
            Map<String, Object> orgJsonObject = new ObjectMapper().readValue(reqBody, HashMap.class);
            Map<String, Object> newJsonObject = new HashMap<>();
            orgJsonObject.forEach((key, value) -> newJsonObject.put(key, SecureUtil.charEscape(value.toString())));
            
            //대체 request를 생성해서 넘김
            wrappedRequest.resetInputStream(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(newJsonObject).getBytes());
        }

        filterChain.doFilter(wrappedRequest, response);
    }
}


 */
