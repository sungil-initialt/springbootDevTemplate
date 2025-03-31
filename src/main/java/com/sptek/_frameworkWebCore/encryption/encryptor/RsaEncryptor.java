package com.sptek._frameworkWebCore.encryption.encryptor;

import com.sptek._frameworkWebCore.encryption.GlobalEncryptor;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.security.*;
import java.util.Base64;

@Slf4j
@Component
public class RsaEncryptor implements StringEncryptor {
    private final String ALGORITHM = "RSA";
    private static KeyPair keyPair;

    // RSA 키쌍 생성
    RsaEncryptor() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyGen.initialize(2048); // 키 길이: 2048bit
        keyPair = keyGen.generateKeyPair();

        //Encryption 에 사용 등록 처리
        GlobalEncryptor.register(GlobalEncryptor.Type.sptRSA, this);
    }

    // 현재의 public 제공
    public static PublicKey getPublicKey() {
        return keyPair.getPublic();
    }

    // 공개 키로 암호화 (public, private 키는 서버 재 기동시 변경 됨으로 encrypt된 데이터를 저장해서는 안된다.)
    @Override
    public String encrypt(String plainText) {
        try {
            PublicKey publicKey = getPublicKey();

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error while RSA encrypting", e);
        }
    }

    // 개인키로 복호화
    @Override
    public String decrypt(String base64Encrypted) {
        try {
            PrivateKey privateKey = keyPair.getPrivate();

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(base64Encrypted));
            return new String(decryptedBytes);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error while RSA decrypting", e);
        }
    }
}
