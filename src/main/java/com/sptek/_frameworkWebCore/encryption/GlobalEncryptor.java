package com.sptek._frameworkWebCore.encryption;

import com.sptek._frameworkWebCore.annotation.Enable_DecryptAuto_At_DtoString;
import jakarta.validation.constraints.NotNull;
import org.jasypt.encryption.StringEncryptor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalEncryptor {
    public enum Type {
        sptAES, sptDES, sptJASYPT, sptRSA;
    }

    private static final String encKeyword = "ENC_";
    private static final Map<String, StringEncryptor> encryptorMap = new ConcurrentHashMap<>();

    public static void register(Type encryptorTypeEnum, StringEncryptor stringEncryptor) {
        encryptorMap.put(encryptorTypeEnum.name(), stringEncryptor);
    }

    public static String encrypt(Type encryptorTypeEnum, @NotNull String plainText) {
        StringEncryptor stringEncryptor = encryptorMap.get(encryptorTypeEnum.name());
        if (stringEncryptor == null) {
            throw new IllegalArgumentException(String.format("%s 는 지원하지 않는 암호화 타입 입니다.", encryptorTypeEnum.name()));
        }
        return String.format("%s%s(%s)", encKeyword, encryptorTypeEnum.name(), stringEncryptor.encrypt(plainText));
    }

//    //dto 의 모든 필드를 검사해서 decrypt 하는 방식 (리소스 다소 낭비)
//    public static <T> T decrypt(@NotNull T dto) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
//        Class<?> clazz = dto.getClass();
//        Object copy = clazz.getDeclaredConstructor().newInstance();
//
//        for (Field field : clazz.getDeclaredFields()) {
//            field.setAccessible(true);
//            Object originalField = field.get(dto);
//
//            if (originalField instanceof String originalFieldValue && originalFieldValue.startsWith(encKeyword)) {
//                String decryptedOriginalFieldValue = decrypt(originalFieldValue);
//                field.set(copy, decryptedOriginalFieldValue);
//            } else if (originalField != null && !field.getType().isPrimitive() && !field.getType().getName().startsWith("java.")) {
//                // 재귀: 내부 DTO도 깊은 복사 + 복호화
//                Object decryptedChildField = decrypt(originalField);
//                field.set(copy, decryptedChildField);
//            } else {
//                // 단순 복사 (String or 기타 primitive-like)
//                field.set(copy, originalField);
//            }
//        }
//
//        @SuppressWarnings("unchecked")
//        T castedCopy = (T) copy;
//        return castedCopy;
//    }

    //이미 확인한 객체는 다시 확인 하지 않도록 처리
    public static <T> T decrypt(@NotNull T dto) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        Set<Object> visited = Collections.newSetFromMap(new IdentityHashMap<>());
        return decryptInternal(dto, visited);
    }

    //dto 의 필드중 @EnableAutoDecrypt_InDtoString 어노테이션이 적용된 필드에 대해서만 decrypt 하는 방식 (리소스 절약) // todo : 복잡한 DTO 에 대해 성능 확인 필요
    private static <T> T decryptInternal(@NotNull T dto, Set<Object> visited)
            throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {

        // 중복 객체 방지
        if (visited.contains(dto)) return dto;
        visited.add(dto);

        Class<?> clazz = dto.getClass();
        Object copy = clazz.getDeclaredConstructor().newInstance();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            Object originalField = field.get(dto);

            if (field.isAnnotationPresent(Enable_DecryptAuto_At_DtoString.class)
                    && originalField instanceof String strValue
                    && strValue.startsWith(encKeyword)) {

                String decrypted = decrypt(strValue);
                field.set(copy, decrypted);
            }

            // 재귀 복호화
            else if (originalField != null
                    && !field.getType().isPrimitive()
                    && !field.getType().getName().startsWith("java.")) {

                Object decryptedChild = decryptInternal(originalField, visited);
                field.set(copy, decryptedChild);
            }

            // 나머지 단순 복사
            else {
                field.set(copy, originalField);
            }
        }

        @SuppressWarnings("unchecked")
        T castedCopy = (T) copy;
        return castedCopy;
    }

    public static String decrypt(@NotNull String encryptedText) {
        for (Type encryptorTypeEnum : Type.values()) {
            String prefix = encKeyword + encryptorTypeEnum.name() + "(";

            if (encryptedText.startsWith(prefix) && encryptedText.endsWith(")")) {
                String extractedEncryptedText = encryptedText.substring(prefix.length(), encryptedText.length() - 1);
                StringEncryptor stringEncryptor = encryptorMap.get(encryptorTypeEnum.name());
                if (stringEncryptor == null) {
                    throw new IllegalArgumentException(String.format("%s 는 지원하지 않는 암호화 타입 입니다.", encryptorTypeEnum.name()));
                }
                return stringEncryptor.decrypt(extractedEncryptedText);
            }
        }
        throw new IllegalArgumentException("지원하지 않는 암호화 타입 입니다.");
    }

}
