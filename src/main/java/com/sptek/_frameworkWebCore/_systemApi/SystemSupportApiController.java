package com.sptek._frameworkWebCore._systemApi;

import com.sptek._frameworkWebCore.annotation.EnableResponseOfApiCommonSuccess_InRestController;
import com.sptek._frameworkWebCore.annotation.EnableResponseOfApiGlobalException_InRestController;
import com.sptek._frameworkWebCore.encryption.GlobalEncryptor;
import com.sptek._frameworkWebCore.encryption.encryptModule.RsaEncryptor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@Slf4j
@RestController
@EnableResponseOfApiCommonSuccess_InRestController
@EnableResponseOfApiGlobalException_InRestController
@Tag(name = "시스템 API", description = "시스템이 기본으로 제공하는 API") //swagger
@RequestMapping(value = {"/api/v1/system-support-api/"})
public class SystemSupportApiController {

    //해당 매핑은 NotEssentialRequestPattern 에 포함되어 있음 (필터 적용이 되지 않는다.)
    @GetMapping("/healthCheck")
    @Operation(summary = "healthCheck", description = "healthCheck 제공", tags = {""}) //swagger
    public Object healthCheck() {
        return "ok";
    }

    @GetMapping("/rsaPublicKeyBase64")
    @Operation(summary = "rsaPublicKeyBase64", description = "rsaPublicKeyBase64 제공", tags = {""}) //swagger
    public Object rsaPublicKeyBase64() {
        String plainText = "originPlainText";
        String encryptedText = GlobalEncryptor.encrypt(GlobalEncryptor.Type.sptRSA, plainText);
        String decryptedText = GlobalEncryptor.decrypt(encryptedText);

        log.debug("plainText: {}, decryptedText: {}, decryptedText: {}", plainText, encryptedText, decryptedText);
        return Base64.getEncoder().encodeToString(RsaEncryptor.getPublicKey().getEncoded());
    }
}
