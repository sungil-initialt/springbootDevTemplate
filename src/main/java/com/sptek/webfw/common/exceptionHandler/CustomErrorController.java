package com.sptek.webfw.common.exceptionHandler;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RequiredArgsConstructor
@Controller
public class CustomErrorController implements ErrorController {
    //Controller 외부 영역에서 발생한 에러(필터쪽이나.. 기타 등등) 처리를 기존 controller 가 처리하는 방식을 그데로 이용하기 위해 구현함

    @NonFinal
    final String ERROR_PATH = "/error";
    @NonFinal
    final String API_PATH = "/api";

    final ViewGlobalExceptionHandler viewGlobalExceptionHandler;
    final ApiGlobalExceptionHandler apiGlobalExceptionHandler;

    @RequestMapping(ERROR_PATH)
    public String handleError(HttpServletRequest request) {
        String originUri = String.valueOf(request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI));
        int errorStatusCode = Integer.parseInt(String.valueOf(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)));
        String message = String.valueOf(request.getAttribute(RequestDispatcher.ERROR_MESSAGE));

        log.debug("CustomErrorController originUri({}), errorStatusCode({}), message({})", originUri, errorStatusCode, message);

        //web 페이지 요청
        if (!originUri.startsWith(API_PATH)) {
            // 404는 @ControllerAdvice 쪽으로 자동 호출됨(여기로 호출되지 않음)
            if (errorStatusCode == 401) {
                return viewGlobalExceptionHandler.handleAccessDeniedException(new Exception(message));
            } else if (errorStatusCode == 403) {
                return viewGlobalExceptionHandler.handleAccessDeniedException(new Exception(message));
            } else if (errorStatusCode == 500) {
                return viewGlobalExceptionHandler.handleUnExpectedException(new Exception(message));
            } else {
                return viewGlobalExceptionHandler.handleUnExpectedException(new Exception(message));
            }
        //API 요청
        } else {
            // api 요청의 경우는 rest 에러 응답으로 처리해야 하기에 restController 쪽으로 재 호출(forward)을 하는 방식으로 처림함
            return "forward:/api/restErrorForward";
        }
    }

    @RequestMapping(value = {""})
    @RestController
    public class RestErrorRedirectController {

        // 단순히 에러 처리만을 위해 사용되는 RestController로 바로 에러를 throw 하게하여 RestController 에러 처리 흐름을 타도록 만듬
        @GetMapping("/api/restErrorForward")
        public String restErrorForward(HttpServletRequest request) throws Exception {
            String originUri = String.valueOf(request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI));
            int errorStatusCode = Integer.parseInt(String.valueOf(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)));
            String message = String.valueOf(request.getAttribute(RequestDispatcher.ERROR_MESSAGE));

            log.debug("RestErrorRedirectController originUri({}), errorStatusCode({}), message({})", originUri, errorStatusCode, message);

            if (errorStatusCode == 401) {
                throw new AccessDeniedException(message);
            } else if (errorStatusCode == 403) {
                throw new AccessDeniedException(message);
            } else if (errorStatusCode == 500) {
                throw new Exception(message);
            } else {
                throw new Exception(message);
            }
        }
    }
}

