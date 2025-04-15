package com.sptek._frameworkWebCore._example.unit.responseError;

import com.sptek._frameworkWebCore.annotation.EnableResponseOfViewGlobalException_InViewController;
import com.sptek._frameworkWebCore.base.exception.ServiceException;
import com.sptek._projectCommon.code.ServiceErrorCodeEnum;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/view/example/", produces = MediaType.TEXT_HTML_VALUE)
@EnableResponseOfViewGlobalException_InViewController

public class ResponseErrorViewController {
    @NonFinal
    private final String pageBasePath = "pages/_example/unit/";

    @GetMapping({"/public/responseError/responseError"})
    public String responseError() {
        return pageBasePath + "responseError";
    }

    //500
    @GetMapping({"/public/responseError/runtimeException"})
    public String runtimeException() {
        if (true) {
            throw new NullPointerException("테스트 를 위해 임의로 발생 시킨 RuntimeException");
        }
        return pageBasePath + "index";
    }

    //400
    @GetMapping({"/public/responseError/serviceException"})
    public String serviceException() {
        if (true) {
            throw new ServiceException(ServiceErrorCodeEnum.DEFAULT_ERROR, "테스를 위해 임의로 발생 시킨 ServiceException");
        }
        return pageBasePath + "index";
    }

    //403
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping({"/public/responseError/authException"})
    public String authException() {
        return pageBasePath + "index";
    }
}
