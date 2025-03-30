package com.sptek._frameworkWebCore.encryption.encryptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
@Component
public class AesEncryptor {
    private final String ALGORITHM = "AES";
    private final String AES_CBC_PADDING = "AES/CBC/PKCS5Padding";

    private final SecretKeySpec secretKey;

    // secret key 로드
    AesEncryptor(@Value("${aesEncryptor.base64SecretKey}") String base64SecretKey) {
        byte[] secretKeyBytes = Base64.getDecoder().decode(base64SecretKey);
        this.secretKey = new SecretKeySpec(secretKeyBytes, ALGORITHM);
    }

    // 암호화 메서드 (IV값이 암호화 값에 내려가는 방식, IV 관리하지 않다도 되서 간편하면서도 보안상 단점은 없음)
    public String encrypt(String plainText) {
        try {
            Cipher cipher = Cipher.getInstance(AES_CBC_PADDING);

            // IV 생성
            byte[] iv = new byte[16];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            // IV와 암호문 결합
            byte[] encryptedWithIv = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, encryptedWithIv, 0, iv.length);
            System.arraycopy(encrypted, 0, encryptedWithIv, iv.length, encrypted.length);

            return String.format("Enc_sptAES(%s)", Base64.getEncoder().encodeToString(encryptedWithIv));
        } catch (Exception e) {
            throw new RuntimeException("Error while AES encrypting", e);
        }
    }

//    // 복호화 메서드 (AlltypeDecryptor 로 통합)
//    public String decrypt(String encryptedText) {
//        try {
//            byte[] decoded = Base64.getDecoder().decode(encryptedText);
//            Cipher cipher = Cipher.getInstance(AES_CBC_PADDING);
//
//            // IV 분리
//            byte[] iv = new byte[16];
//            System.arraycopy(decoded, 0, iv, 0, iv.length);
//            IvParameterSpec ivSpec = new IvParameterSpec(iv);
//
//            // 암호문 분리
//            byte[] encrypted = new byte[decoded.length - iv.length];
//            System.arraycopy(decoded, iv.length, encrypted, 0, encrypted.length);
//
//            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
//            byte[] original = cipher.doFinal(encrypted);
//
//            return new String(original, StandardCharsets.UTF_8);
//        } catch (Exception e) {
//            throw new RuntimeException("Error while AES decrypting", e);
//        }
//    }
}
