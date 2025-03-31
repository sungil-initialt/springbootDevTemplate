package com.sptek._frameworkWebCore.encryption.encryptor;

import com.sptek._frameworkWebCore.encryption.GlobalEncryptor;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
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
// todo: DES 는 보안성이 취약함으로 꼭 사용할 이유가 있을때 만 사용 할것
public class DesEncryptor implements StringEncryptor {
    private final String ALGORITHM = "DES";
    private final String TRANSFORMATION = "DES/CBC/PKCS5Padding";

    private final SecretKeySpec secretKey;

    // Secret key 로드
    DesEncryptor(@Value("${desEncryptor.base64SecretKey}") String base64SecretKey) {
        byte[] secretKeyBytes = Base64.getDecoder().decode(base64SecretKey);
        if (secretKeyBytes.length != 8) {
            // DES 키는 반드시 8바이트 (64비트)여야 함
            throw new IllegalArgumentException("Error while DES encrypting, DES key length must be an 11-character Base64-encoded (64 bits)");
        }
        this.secretKey = new SecretKeySpec(secretKeyBytes, ALGORITHM);

        //Encryption 에 사용 등록 처리
        GlobalEncryptor.register(GlobalEncryptor.Type.sptDES, this);
    }

    // 암호화 메서드
    @Override
    public String encrypt(String plainText) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            // 8바이트 IV 생성
            byte[] iv = new byte[8];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            // IV와 암호문 결합
            byte[] encryptedWithIv = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, encryptedWithIv, 0, iv.length);
            System.arraycopy(encrypted, 0, encryptedWithIv, iv.length, encrypted.length);

            return Base64.getEncoder().encodeToString(encryptedWithIv);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error while DES encrypting", e);
        }
    }

    // 복호화 메서드 (AllTypeDecryptor 로 통합)
    @Override
    public String decrypt(String encryptedText) {
        try {
            byte[] decoded = Base64.getDecoder().decode(encryptedText);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            // 8바이트 IV 분리
            byte[] iv = new byte[8];
            System.arraycopy(decoded, 0, iv, 0, iv.length);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // 암호문 분리
            byte[] encrypted = new byte[decoded.length - iv.length];
            System.arraycopy(decoded, iv.length, encrypted, 0, encrypted.length);

            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            byte[] original = cipher.doFinal(encrypted);

            return new String(original, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error while DES decrypting", e);
        }
    }
}
