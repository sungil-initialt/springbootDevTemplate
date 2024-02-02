package com.sptek.webfw.support;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/*
모든 Controller는 CommonControllerSupport를 상속하여 구현함.
Controller에서 이용할수 있는 공통 기능을 추가해 나가면 됨
 */
@Slf4j
@Controller
public class CommonControllerSupport {
    @Autowired
    private Environment environment;

    private HttpServletRequest getCurrenRequest(){
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }

    private HttpServletResponse getCurrenResponse(){
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
    }
}
