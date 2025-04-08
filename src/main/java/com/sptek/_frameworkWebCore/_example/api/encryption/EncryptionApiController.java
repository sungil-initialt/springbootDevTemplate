package com.sptek._frameworkWebCore._example.api.encryption;

import com.sptek._frameworkWebCore.annotation.EnableDecryptAuto_InDtoString;
import com.sptek._frameworkWebCore.annotation.EnableResponseOfApiCommonSuccess_InRestController;
import com.sptek._frameworkWebCore.annotation.EnableResponseOfApiGlobalException_InRestController;
import com.sptek._frameworkWebCore.encryption.GlobalEncryptor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@EnableResponseOfApiCommonSuccess_InRestController
@EnableResponseOfApiGlobalException_InRestController
@RequestMapping(value = {"/api/v1/example/encryption/"}, produces = {MediaType.APPLICATION_JSON_VALUE/*, MediaType.APPLICATION_XML_VALUE*/}) // 클라이언트가 Accept 해더를 보낼 경우 제공하는 미디어 타입이 일치해야함(없으면 406)
@Tag(name = "encryption", description = "")

public class EncryptionApiController {

    @Value("${jasypt.decryptTest.encValue}") // 프로퍼티에 jasypt 암호화 값으로 저장되 있음
    private String encValue;

    @GetMapping("/JasyptForProperty")
    @Operation(summary = "서버 프로퍼티의 jasypt Encrypted 값이 Decrypted 되어 바인딩 되는지 확인 ", description = "", tags = {""})
    public Object JasyptForProperty() {
        return encValue;
    }

    @PostMapping("/allTypeEncryptForString")
    @Operation(summary = "plain text 에 대해 DES, AES, RSA, Jasypt 로 암/복호화", description = "", tags = {""})
    public Object allTypeEncryptForString(
            @Parameter(name = "plainText", description = "암호화 할 plainText") @RequestBody String plainText)
    {
        HashMap<String, String> encryptedMap = new HashMap<>();
        encryptedMap.put(GlobalEncryptor.Type.sptDES.name(), GlobalEncryptor.encrypt(GlobalEncryptor.Type.sptDES, plainText)); // 보안 취약
        encryptedMap.put(GlobalEncryptor.Type.sptAES.name(), GlobalEncryptor.encrypt(GlobalEncryptor.Type.sptAES, plainText));
        encryptedMap.put(GlobalEncryptor.Type.sptRSA.name(), GlobalEncryptor.encrypt(GlobalEncryptor.Type.sptRSA, plainText)); // RSA는 저장하는 용도로 사용 하지 말것
        encryptedMap.put(GlobalEncryptor.Type.sptJASYPT.name(), GlobalEncryptor.encrypt(GlobalEncryptor.Type.sptJASYPT, plainText));

        HashMap<String, String> decryptedMap = new HashMap<>();
        decryptedMap.put(GlobalEncryptor.Type.sptDES.name(), GlobalEncryptor.decrypt(encryptedMap.get(GlobalEncryptor.Type.sptDES.name())));
        decryptedMap.put(GlobalEncryptor.Type.sptAES.name(), GlobalEncryptor.decrypt(encryptedMap.get(GlobalEncryptor.Type.sptAES.name())));
        decryptedMap.put(GlobalEncryptor.Type.sptRSA.name(), GlobalEncryptor.decrypt(encryptedMap.get(GlobalEncryptor.Type.sptRSA.name())));
        decryptedMap.put(GlobalEncryptor.Type.sptJASYPT.name(), GlobalEncryptor.decrypt(encryptedMap.get(GlobalEncryptor.Type.sptJASYPT.name())));

        Map<String, Object> resultMap = new LinkedHashMap<>();
        resultMap.put("plainText", plainText);
        resultMap.put("encryptedMap", encryptedMap);
        resultMap.put("decryptedMap", decryptedMap);
        return resultMap;
    }

    @PostMapping("/allTypeDecryptForString")
    @Operation(summary = "DES, AES, RSA, Jasypt 로 Encrypt 된 String 의 복호화", description = "")
    public Object allTypeDecryptForString(
            @Parameter(name = "encryptText", description = "암호화된(DES, AES, RSA, Jasypt) 텍스트") @RequestBody String encryptText)
    {
        return GlobalEncryptor.decrypt(encryptText);
    }

    @PostMapping("/allTypeDecryptForDto")
    @Operation(summary = "DES, AES, RSA, Jasypt 로 Encrypt 한 필드를 포함 하는 DTO 의 복호화", description = "")
    public Object allTypeDecryptForDto(
            @Parameter(name = "parentDto", description = "암호화된(DES, AES, RSA, Jasypt) 값을 포함하는 DTO") @RequestBody ParentDto parentDto) throws Exception
    {
        return GlobalEncryptor.decrypt(parentDto);
    }





    // 테스를 위한 임의 DTOs ================================================================
    @Data
    public static class ParentDto {
        @EnableDecryptAuto_InDtoString
        @Schema(example = "ENC_sptJASYPT(Ux9vDuJKHsiyR37oKh2ivn6C4cNOftTu07ZosIJmd4ACCs3qSc8vxNd8hWWuD9PH)")
        private String field1;
        private String field2;
        private ChileDto field3 = new ChileDto();
    }

    @Data
    public static class ChileDto {
        @EnableDecryptAuto_InDtoString
        @Schema(example = "ENC_sptDES(/QUmzFi1GuzK5BnAZ1RrNxtcUMMUx+yU)")
        private String field1;

        @EnableDecryptAuto_InDtoString
        @Schema(example = "ENC_sptAES(i7SNnO3OyWwC5CbizW5OovemxzXdFhPMbt1uuEG+kRg=)")
        private String field2;

    }
}
