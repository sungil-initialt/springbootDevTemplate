package com.sptek.webfw.config;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
class JasyptConfigTest {


//    @MockBean
//    StandardPBEStringEncryptor standardPBEStringEncryptor;
//
//    @Test
//    @DisplayName("testJasyptEncDec")
//    public void testJasyptEncDec(){
//        String plainText = "hello world";
//
//        //StandardPBEStringEncryptor standardPBEStringEncryptor = new StandardPBEStringEncryptor();
//        String encryptedText = standardPBEStringEncryptor.encrypt(plainText);
//        String rePlainText = standardPBEStringEncryptor.decrypt(encryptedText);
//
//        Assertions.assertEquals(plainText, plainText, plainText);
//        Assertions.assertEquals(encryptedText, encryptedText, encryptedText);
//        Assertions.assertEquals(plainText, rePlainText, rePlainText);
//
//    }
}