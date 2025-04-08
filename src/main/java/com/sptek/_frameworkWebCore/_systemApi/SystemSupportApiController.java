package com.sptek._frameworkWebCore._systemApi;

import com.sptek._frameworkWebCore.annotation.EnableResponseOfApiCommonSuccess_InRestController;
import com.sptek._frameworkWebCore.annotation.EnableResponseOfApiGlobalException_InRestController;
import com.sptek._frameworkWebCore.encryption.GlobalEncryptor;
import com.sptek._frameworkWebCore.encryption.encryptModule.RsaEncryptor;
import com.sptek._frameworkWebCore.globalVo.ProjectInfoVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@Slf4j
@RestController
@RequiredArgsConstructor
@EnableResponseOfApiCommonSuccess_InRestController
@EnableResponseOfApiGlobalException_InRestController
@RequestMapping(value = {"/api/v1/systemSupportApi/"})
@Tag(name = "systemSupportApi", description = "")

public class SystemSupportApiController {
    private final ProjectInfoVo projectInfoVo;

    @GetMapping("/projectInfo")
    @Operation(summary = "프로퍼티 설정된 프로젝트 기본 정보 제공", description = "", tags = {""})
    public Object projectInfo() {
        return projectInfoVo;
    }

    @GetMapping("/healthCheck")
    @Operation(summary = "healthCheck api", description = "", tags = {""}) //swagger
    public Object healthCheck() {
        return "ok";
    }

    @GetMapping("/rsaPublicKeyBase64")
    @Operation(summary = "클라이언트 RSA 암호화를 위한 public key 제공 api", description = "", tags = {""}) //swagger
    public Object rsaPublicKeyBase64() {
        String plainText = "originPlainText";
        String encryptedText = GlobalEncryptor.encrypt(GlobalEncryptor.Type.sptRSA, plainText);
        String decryptedText = GlobalEncryptor.decrypt(encryptedText);

        log.debug("plainText: {}, decryptedText: {}, decryptedText: {}", plainText, encryptedText, decryptedText);
        return Base64.getEncoder().encodeToString(RsaEncryptor.getPublicKey().getEncoded());
    }
}
