package com.sptek.webfw.base.exceptionHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Slf4j
@RequiredArgsConstructor
@Controller
public class CustomApplicationErrorController implements ErrorController {
    private final ObjectMapper objectMapper;
    //Controller 외부 영역에서 발생한 에러(필터쪽이나.. 기타 등등)를 직접 처리하기 위해 내부적으로 스프링이 디폴트로 처리하던 "/error" 매핑을 직접 커스터마이징 구현함

    @RequestMapping("/error") //프로퍼티 내 server.error.path 와 동일한 값으로 설정
    public Object handleError(HttpServletRequest request, HttpServletResponse response) throws Throwable {

        /*
        // todo: 아래 방식으로 ex 바로 throw 하는 방법을 생각했으나.. RequestDispatcher.ERROR_STATUS_CODE 는 있는데 RequestDispatcher.ERROR_EXCEPTION 이 정확히 들어 오지 않는 케이스가 있음
        String errorRequestUri = Optional.ofNullable(request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI))
                .map(Object::toString)
                .orElse("");
        String errorExceptionName = Optional.ofNullable(request.getAttribute(RequestDispatcher.ERROR_EXCEPTION))
                .map(object -> object instanceof Throwable ? ((Throwable) object).getClass().getSimpleName() : object.getClass().getSimpleName())
                .orElse("");
        String errorMessage = Optional.ofNullable(request.getAttribute(RequestDispatcher.ERROR_EXCEPTION))
                .map(object -> object instanceof Throwable ? ((Throwable) object).getMessage() : "")
                .orElse("");
        Integer errorStatusCode = Optional.ofNullable(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
                .map(object -> Integer.valueOf(object.toString()))
                .orElse(null);
        log.error("Higher-level Error occurred: errorRequestUri({}), errorException({}), errorStatusCode({}), errorMessage({})", errorRequestUri, errorExceptionName, errorStatusCode, errorMessage);

        throw (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        */

        // 404 에러의 경우도 여길 탈것 같지만.. 바로 ApplicationGlobalExceptionHandler 가 호출됨..
        // 일부러 Exception 을 발생시켜 ApplicationGlobalExceptionHandler 가 처리하도록 만든것임
        int errorStatusCode = Integer.parseInt(String.valueOf(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)));
        String errMessage = String.valueOf(request.getAttribute(RequestDispatcher.ERROR_MESSAGE));
        log.debug("errorStatusCode({}), message({})", errorStatusCode, errMessage);

        if (errorStatusCode == 401) {
            throw new AccessDeniedException(errMessage);

        } else if (errorStatusCode == 403) {
            throw new AccessDeniedException(errMessage);

        } else if (errorStatusCode == 500) {
            throw new Exception(errMessage);

        } else {
            throw new Exception(errMessage);
        }
    }
}

