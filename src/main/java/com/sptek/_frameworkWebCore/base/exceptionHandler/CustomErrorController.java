package com.sptek._frameworkWebCore.base.exceptionHandler;

import com.sptek._frameworkWebCore.annotation.EnableResponseOfApplicationGlobalException_InMain;
import com.sptek._frameworkWebCore.annotation.annotationCondition.HasAnnotationOnMain_InBean;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;


@Slf4j
@RequiredArgsConstructor
@HasAnnotationOnMain_InBean(EnableResponseOfApplicationGlobalException_InMain.class)
@ConditionalOnProperty(name = "server.error.ignoreCustomErrorViewForDebug", havingValue = "false", matchIfMissing = false)
@Controller

public class CustomErrorController implements ErrorController {
    //Controller 외부 영역 에서 발생한 에러(필터 쪽이나.. 기타 등등)를 직접 처리 하기 위해 ErrorController 상속 받아 구현 함 (정확히 는 controller 에 별도 에러 핸들러 가 없다면 그때는 모두 이곳 으로 진입)
    //해당 Controller 가 없다면 스프링 이 내부 디폴트 로직에 따라 "/error" 리소내 errcode.html 로 자동 매핑 해준다.

    //private final ObjectMapper objectMapper;
    @RequestMapping("/error") //프로퍼티 내 server.error.path 와 동일한 값으로 설정
    public Object handleError(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        log.debug("Higher-level Error occurred: requestUri({}), methodType({}), url({}), params({})", request.getRequestURI(), request.getMethod(), request.getRequestURL(), request.getParameterMap());

        /*
        // todo: 아래 방식으로 ex를 바로 throw 하는 방법을 생각했으나.. RequestDispatcher.ERROR_STATUS_CODE 는 있는데 RequestDispatcher.ERROR_EXCEPTION 이 정확히 들어 오지 않는 케이스가 있음
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

        // todo: 에러 메시지 가 안 나오는 케이스 에 대해 좀더 확인 필요
        int errorStatusCode = Integer.parseInt(String.valueOf(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)));
        Object errorMsgAttr = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        String errMessage = (errorMsgAttr != null && !errorMsgAttr.toString().isBlank())
                ? errorMsgAttr.toString()
                : null;

        if (errMessage == null) {
            Throwable throwable = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
            if (throwable != null) {
                errMessage = Optional.ofNullable(throwable.getMessage()).orElse(throwable.getClass().getSimpleName());
            } else {
                errMessage = "No error message available";
            }
        }

        log.debug("errorStatusCode({}), message({})", errorStatusCode, errMessage);

        // 상위 레벨 에서 발생할 수 있는 에러의 종류를 이정도 로 정의함(더 구체화 가능)
        if (errorStatusCode == 401) {
            throw new AuthenticationException(errMessage) {
                @Override
                public String getMessage() {
                    return super.getMessage();
                }
            };

        } else if (errorStatusCode == 403) {
            throw new AccessDeniedException(errMessage);

        } else if (errorStatusCode == 404) {
            // todo : 적절한 Exception 이 없어서 ResponseStatusException 로 처림함(명확성은 좀 떨어짐)..
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, errMessage);

        } else if (errorStatusCode == 405) {
            throw new HttpRequestMethodNotSupportedException(errMessage);

        } else if (errorStatusCode == 413) {
            // todo: 413 이 이곳 에서 케치 되지 않고 있음..
            throw new MaxUploadSizeExceededException(0);

        } else {
            throw new Exception(errMessage);
        }
    }
}

