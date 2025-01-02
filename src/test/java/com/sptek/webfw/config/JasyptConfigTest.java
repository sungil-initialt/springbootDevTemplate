package com.sptek.webfw.config;

import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
class JasyptConfigTest {

    @Autowired
    @Qualifier(value = "jasyptStringEncryptor")
    private StringEncryptor stringEncryptor;


    @Test
    void testStringEncryptionAndDecryption() {
        // 테스트용 문자열
        String plainText = "plainTest-abc";

        // 암호화
        String encryptedText = stringEncryptor.encrypt(plainText);
        log.debug("Encrypted Text: {}", encryptedText);

        // 복호화
        String decryptedText = stringEncryptor.decrypt(encryptedText);
        log.debug("Decrypted Text: {}", decryptedText);

        // 원본 텍스트와 복호화된 텍스트가 같은지 검증
        assertThat(decryptedText).isEqualTo(plainText);
    }
}