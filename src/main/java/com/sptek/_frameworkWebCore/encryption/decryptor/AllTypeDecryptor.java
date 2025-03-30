package com.sptek._frameworkWebCore.encryption.decryptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Component
public class AllTypeDecryptor {
    private final String aesALGORITHM = "AES";
    private final String AES_CBC_PADDING = "AES/CBC/PKCS5Padding";
    private final SecretKeySpec secretKeyForAes;

    private final String desALGORITHM = "DES";
    private final String TRANSFORMATION = "DES/CBC/PKCS5Padding";
    private final SecretKeySpec secretKeyForDes;

    // secret key 로드
    AllTypeDecryptor(@Value("${aesEncryptor.base64SecretKey}") String base64SecretKeyForAes, @Value("${desEncryptor.base64SecretKey}") String base64SecretKeyForDes) {
        //AES
        byte[] secretKeyBytesForAes = Base64.getDecoder().decode(base64SecretKeyForAes);
        this.secretKeyForAes = new SecretKeySpec(secretKeyBytesForAes, aesALGORITHM);

        //DES
        byte[] secretKeyBytesForDes = Base64.getDecoder().decode(base64SecretKeyForDes);
        if (secretKeyBytesForDes.length != 8) {
            // DES 키는 반드시 8바이트 (64비트)여야 함
            throw new IllegalArgumentException("Error while DES encrypting, DES key length must be an 11-character Base64-encoded (64 bits)");
        }
        this.secretKeyForDes = new SecretKeySpec(secretKeyBytesForDes, desALGORITHM);
    }

    public String decryptAES(String encryptedText) {
        try {
            
            byte[] decoded = Base64.getDecoder().decode(extractEncPayload(encryptedText, "Enc_sptAES"));
            Cipher cipher = Cipher.getInstance(AES_CBC_PADDING);

            // IV 분리
            byte[] iv = new byte[16];
            System.arraycopy(decoded, 0, iv, 0, iv.length);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // 암호문 분리
            byte[] encrypted = new byte[decoded.length - iv.length];
            System.arraycopy(decoded, iv.length, encrypted, 0, encrypted.length);

            cipher.init(Cipher.DECRYPT_MODE, secretKeyForAes, ivSpec);
            byte[] original = cipher.doFinal(encrypted);

            return new String(original, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error while AES decrypting", e);
        }
    }

    public String decryptDES(String encryptedText) {
        try {
            byte[] decoded = Base64.getDecoder().decode(extractEncPayload(encryptedText, "Enc_sptDES"));
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            // 8바이트 IV 분리
            byte[] iv = new byte[8];
            System.arraycopy(decoded, 0, iv, 0, iv.length);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // 암호문 분리
            byte[] encrypted = new byte[decoded.length - iv.length];
            System.arraycopy(decoded, iv.length, encrypted, 0, encrypted.length);

            cipher.init(Cipher.DECRYPT_MODE, secretKeyForDes, ivSpec);
            byte[] original = cipher.doFinal(encrypted);

            return new String(original, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error while DES decrypting", e);
        }
    }

    private String extractEncPayload(String encryptedText, String prefix) {
        int prefixLength = prefix.length();
        if (!encryptedText.startsWith(prefix + "(") || !encryptedText.endsWith(")")) {
            //log.error("Encrypt 형식이 정확하지 않습니다: " + encryptedText);
            throw new IllegalArgumentException();
        }

        // 괄호 안 값만 추출
        return encryptedText.substring(prefixLength + 1, encryptedText.length() - 1);
    }

    public String decryptAllType(String encryptedText) {
        String decryptedText;

        if (encryptedText.startsWith("Enc_sptAES")) {
            decryptedText = decryptAES(encryptedText);
        } else if (encryptedText.startsWith("Enc_sptDES")) {
            decryptedText = decryptDES(encryptedText);
        } else {
            throw new IllegalArgumentException("지원하지 않는 암호화 형식 입니다 : " + encryptedText);
        }
        return decryptedText;
    }

    public Object decryptDtoAllType(Object dto) throws IllegalAccessException {
        for (Field field : dto.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(dto);

            if (value instanceof String strVal && strVal.startsWith("Enc_spt")) {
                String decryptedText = decryptAllType(strVal);
                field.set(dto, decryptedText);
            }
            // 재귀: 내부 DTO 필드일 수 있음
            else if (value != null && !field.getType().isPrimitive() && !field.getType().getName().startsWith("java.")) {
                decryptDtoAllType(value);
            }
        }

        return dto;
    }
}
