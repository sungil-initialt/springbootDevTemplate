package com.sptek._frameworkWebCore._systemController;

import com.sptek._frameworkWebCore.annotation.Enable_ResponseOfApiCommonSuccess_At_RestController;
import com.sptek._frameworkWebCore.annotation.Enable_ResponseOfApiGlobalException_At_RestController;
import com.sptek._frameworkWebCore.annotation.Enable_ResponseOfViewGlobalException_At_ViewController;
import com.sptek._frameworkWebCore.encryption.encryptModule.RsaEncryptor;
import com.sptek._frameworkWebCore.commonObject.vo.ProjectInfoVo;
import com.sptek._frameworkWebCore.springSecurity.CustomAuthenticationSuccessHandlerForView;
import com.sptek._frameworkWebCore.springSecurity.spt.RedirectHelperAfterLogin;
import com.sptek._frameworkWebCore.util.LocaleUtil;
import com.sptek._frameworkWebCore.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.util.Base64;

@Slf4j
@RestController
@RequiredArgsConstructor
@Enable_ResponseOfApiCommonSuccess_At_RestController
@Enable_ResponseOfApiGlobalException_At_RestController
@RequestMapping(value = {"/systemSupportApi/"}, produces = {MediaType.APPLICATION_JSON_VALUE/*, MediaType.APPLICATION_XML_VALUE*/}) // 클라이언트 가 Accept 해더를 보낼 경우 제공 하는 미디어 타입이 일치 해야함(없으면 406)
@Tag(name = "System Support API", description = "")

public class SystemSupportController {
    // todo: Req 매핑 경로에 "/notEssential/" 를 포함하면 불필요한 필터 및 로그 처리를 방지 할 수 있다.

    private final ProjectInfoVo projectInfoVo;

    @GetMapping("/projectInfo")
    @Operation(summary = "프로퍼티 설정된 프로젝트 기본 정보 제공", description = "")
    public Object projectInfo() {
        return projectInfoVo;
    }

    @GetMapping("/serverName")
    @Operation(summary = "시스템 환경 설정에 따른 서버 네임 제공", description = "")
    public Object propertyConfigImport(@Parameter(hidden = true) @Value("${server.name}") String serverName) {
        return serverName;
    }

    //notEssential 를 경로에 포함하여 주요 필터 및 logging 처리를 막는다.
    @GetMapping("/notEssential/healthCheck")
    @Operation(summary = "healthCheck 용 api 제공", description = "") //swagger
    public Object healthCheck() {
        return "ok";
    }

    @GetMapping("/rsaPublicKeyBase64")
    @Operation(summary = "클라이언트 RSA 암호화를 위한 public key 제공", description = "") //swagger
    public Object rsaPublicKeyBase64() {
        return Base64.getEncoder().encodeToString(RsaEncryptor.getPublicKey().getEncoded());
    }

    @GetMapping("/supportedLocaleLanguage")
    @Operation(summary = "지원 하는 Locale/Language 목록 제공", description = "") //swagger
    public Object supportedLocaleLanguage() {
        return LocaleUtil.getMajorLocales();
    }


    @GetMapping(value = "/fileByteFromStorage")
    @Operation(summary = "File 에 대한 바이트 스트림 제공 (권한 처리 포함) ", description = "") //swagger
    public Object fileByteFromStorage(@RequestParam("securedFilePath") String securedFilePath)  throws Exception {
        // 테스트 securedFilePath >> SELECT CONCAT(FILE_PATH, '/', FILE_NAME) AS FULL_PATH FROM POST_FILES;
        return ResponseUtil.makeResponseEntityFromFile(Path.of(securedFilePath));
    }

    @Controller
    @RequiredArgsConstructor
    @RequestMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    @Enable_ResponseOfViewGlobalException_At_ViewController
    public class SystemSupportViewController {

        @GetMapping({"/"})
        public String index() {
            // todo: 무엇 으로 처리 할까?
            //return "pages/_example/unit/index"; // 직접 페이지 이동
            return "redirect:/view/index"; //리다이렉트 방식
            //return "forward:/view/index"; //포워딩 방식
        }

        @GetMapping({"/view/index"})
        public String indexForward() {
            return "pages/_example/unit/index";
        }

        //@GetMapping({"/login", "/signup"}) // todo: 왜 signup을 추가했지?
        // 공통 기능 으로 보아 이곳에 작성
        @GetMapping({"/view/login"})
        public String login(Model model , HttpServletRequest request, HttpServletResponse response) {
            model.addAttribute(CustomAuthenticationSuccessHandlerForView.LOGIN_SUCCESS_TARGETURL_PARAMETER, RedirectHelperAfterLogin.getRedirectUrlAfterLogging(request, response));
            return  "pages/_systemSupport/login";
        }

        @RequestMapping("/view/error/{errViewName}")
        public String error(@PathVariable("errViewName") String errViewName) {
            //해당 RequestMapping 은 에러 페이지 들의 UI 개발 및 체크등 위한 용도 이며 실제 상황 에서 ex 발생시 정상 적인 메커니즘을 통해 에러 페이지 로 연결 된다.

            /*
            //에러코드별 대표 표이지로 처리할 수도 있다.
            errorCode =  switch (errorCode) {
                case "400", "404" -> "4xx";
                case "500", "501" -> "error-5xx";
                default -> "commonInternalError";
            };
            return "error/" + errorCode;
            */

            return "error/" + errViewName;
        }
    }
}
