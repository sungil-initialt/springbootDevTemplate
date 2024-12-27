package com.sptek.webfw.common.exceptionHandler;

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
public class CustomErrorController implements ErrorController {
    //Controller 외부 영역에서 발생한 에러(필터쪽이나.. 기타 등등)를 직접 처리하기 위해 스프링이 디폴트로 처리하던 "/error" 매핑을 직접 커스터마이징 구현함

    @RequestMapping("/error") //프로퍼티 server.error.path 와 동일한 값으로 설정
    public Object handleError(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int errorStatusCode = Integer.parseInt(String.valueOf(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)));
        String message = String.valueOf(request.getAttribute(RequestDispatcher.ERROR_MESSAGE));

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
    }
}

