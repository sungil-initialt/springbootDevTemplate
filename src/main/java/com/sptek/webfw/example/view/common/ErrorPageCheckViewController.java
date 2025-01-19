package com.sptek.webfw.example.view.common;

import com.sptek.webfw.anotation.EnableViewCommonErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/*
해당 컨트롤러는 error 페이지의 UI를 단순히 확인하기 위한 용도이다.(이 컨트롤러가 없으면 페이지 UI를 확인하기 위해 실제 에러를 발생해야 함)
 */
@Slf4j
@Controller
@EnableViewCommonErrorResponse
public class ErrorPageCheckViewController {
    @RequestMapping("/error/{errViewName}")
    public String error(@PathVariable("errViewName") String errViewName) {
        //해당 RequestMapping은 UI확인등 에러 페이지를 눈으로 확인하기 위한 용도이며 실제 상황에서 ex 발생시 정상적인 메커니즘을 통해 에러 페이지로 연결된다.
        log.debug("errViewName : {} ", errViewName);

        /*
        //에러코드별 대표 표이지로 처리할 수도 있다.
        errorCode =  switch (errorCode) {
            case "400", "404" -> "4xx";
            case "500", "501" -> "error-5xx";
            default -> "commonInternalErrorView";
        };
        return "error/" + errorCode;
        */

        return "error/" + errViewName;
    }
}
