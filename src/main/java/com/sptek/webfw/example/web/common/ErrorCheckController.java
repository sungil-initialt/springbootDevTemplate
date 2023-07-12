package com.sptek.webfw.example.web.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
public class ErrorCheckController {
    @RequestMapping("/error/{errorCode}")
    public String error(@PathVariable String errorCode) {
        /*해당 매핑은 에러 페이지를 눈으로 확인하기 위한 용도이며 실제 상황에서 에러 페이지가 보여지는 메커니즘에서는 해당 매핑을 호출 하지 않는다.*/

        log.debug("called error page");

        /*
        String errPage =  switch (errorCode) {
            case "400", "404" -> "error-4xx";
            case "500", "501" -> "error-5xx";
            default -> "error";
        };
        return "error/" + errPage;
        */
        return "error/" + errorCode;
    }
}
