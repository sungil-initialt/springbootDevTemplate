package com.sptek.webfw.common.exceptionHandler;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Slf4j
@RequiredArgsConstructor
@Controller
public class CustomErrorController implements ErrorController {
    //Controller 외부 영역에서 발생한 에러(필터쪽이나.. 기타 등등) 처리를 기존 controller 가 처리하는 방식을 그데로 이용하기 위해 스프링이 직접 처리하던 "/error" 매핑을 직접 커스터마이징 구현함

    @NonFinal
    final String ERROR_PATH = "/error";
    @NonFinal
    final String API_PATH = "/api";

    @RequestMapping(ERROR_PATH)
    public Object handleError(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String originUri = String.valueOf(request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI));
        int errorStatusCode = Integer.parseInt(String.valueOf(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)));
        String message = String.valueOf(request.getAttribute(RequestDispatcher.ERROR_MESSAGE));

        log.debug("CustomErrorController originUri({}), errorStatusCode({}), message({})", originUri, errorStatusCode, message);
        //todo: 404 에러의 경우는 이 매핑도 타지 않고 바로 GlobalExceptionHandler 가 호출됨.. (문제는 없으나 확인 필요)
        if (errorStatusCode == 401) {
            throw new AccessDeniedException(message);
        } else if (errorStatusCode == 403) {
            throw new AccessDeniedException(message);
        } else if (errorStatusCode == 500) {
            throw new Exception(message);
        } else {
            throw new Exception(message);
        }

// todo: 포워딩 처리되면서 req, res 다른 객체로 랩핑디는 문제가 있어 일단 보류 함
//        //web 페이지 요청
//        if (!originUri.startsWith(API_PATH)) {
//            // 404는 @ControllerAdvice 쪽으로 자동 호출됨(여기로 호출되지 않음)
//            if (errorStatusCode == 401) {
//                throw new AccessDeniedException(message);
//            } else if (errorStatusCode == 403) {
//                throw new AccessDeniedException(message);
//            } else if (errorStatusCode == 500) {
//                throw new Exception(message);
//            } else {
//                throw new Exception(message);
//            }
//        //API 요청
//        } else {
//            // api 요청의 경우는 rest 에러 응답으로 처리해야 하기에 restController 쪽으로 재 호출(forward)을 하는 방식으로 처림함
//            return "forward:/api/v1/restErrorForward"; //
//        }


    }

// todo: 포워딩 처리되면서 req, res 다른 객체로 랩핑디는 문제가 있어 일단 보류 함
//    @RequestMapping(value = {"/api/v1/"})
//    @RestController
//    public class RestErrorForwardController {
//
//        // 단순히 에러 처리만을 위해 사용되는 RestController로 바로 에러를 throw 하게하여 RestController 에러 처리 흐름을 타도록 만듬
//        @GetMapping("/restErrorForward")
//        public void restErrorForward(HttpServletRequest request) throws Exception {
//            String originUri = String.valueOf(request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI));
//            int errorStatusCode = Integer.parseInt(String.valueOf(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)));
//            String message = String.valueOf(request.getAttribute(RequestDispatcher.ERROR_MESSAGE));
//
//            log.debug("RestErrorRedirectController originUri({}), errorStatusCode({}), message({})", originUri, errorStatusCode, message);
//
//            if (errorStatusCode == 401) {
//                throw new AccessDeniedException(message);
//            } else if (errorStatusCode == 403) {
//                throw new AccessDeniedException(message);
//            } else if (errorStatusCode == 500) {
//                throw new Exception(message);
//            } else {
//                throw new Exception(message);
//            }
//        }
//    }
}

