package com.sptek.webfw.config;

import com.sptek.webfw.config.encryption.JasyptConfig;
import com.sptek.webfw.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
class JasyptConfigTest {
    @Autowired
    @Qualifier("jasyptStringEncryptor")
    StringEncryptor stringEncryptor;

    @Test
    @DisplayName("testJasyptEncDec")
    public void testJasyptEncDec(){

        String key = "my_jasypt_key";
        StandardPBEStringEncryptor pbeEnc = new StandardPBEStringEncryptor();
        pbeEnc.setAlgorithm("PBEWithMD5AndDES");
        pbeEnc.setPassword(key);

        String plainText = "hello world";
        String encryptedText = pbeEnc.encrypt(plainText);
        String rePlainText = pbeEnc.decrypt(encryptedText);
        log.debug("plainText({}), encryptedText({}), rePlainText({})", plainText, encryptedText, rePlainText);

        //Assertions.assertEquals(plainText, rePlainText, "올바르게 복호화 되지 않음");

    }
}