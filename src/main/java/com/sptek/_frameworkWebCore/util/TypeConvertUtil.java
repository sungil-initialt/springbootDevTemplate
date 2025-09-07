package com.sptek._frameworkWebCore.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/*
오브젝트 또는 데이터 타입과 관련한 변경 유틸
 */
@Slf4j
public class TypeConvertUtil {
    private static ObjectMapper objectMapperWithRootName = null; //

    public static String strMapToString(Map<String, String> originMap){
        return originMap.entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(", "));
    }

    public static String strArrMapToString(Map<String, String[]> originMap){
        return originMap.entrySet().stream()
                .map(entry -> entry.getKey() + ":" + Arrays.toString(entry.getValue()))
                .collect(Collectors.joining(", "));
    }

    public static String strListMapToString(Map<String, List<String>> originMap) {
        return originMap.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(", "));
    }

    public static String objectToJsonWithoutRootName(@Nullable Object object, boolean prettyPrintOption) throws JsonGenerationException, JsonMappingException, IOException {
        if (object == null) {
            return "{}";

        } else {
            if (prettyPrintOption) {
                return SpringUtil.getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(object);
            } else {
                return SpringUtil.getObjectMapper().writeValueAsString(object);
            }
        }
    }

    public static String objectToJsonWithRootName(Object object, boolean prettyPrintOption) throws JsonGenerationException, JsonMappingException, IOException {
        // config 변경 (쓰레드 세이프하지 않음)이 필요함으로 빈이 아닌 별도 생성하여 사용
        if (objectMapperWithRootName == null) {
            objectMapperWithRootName = new ObjectMapper();
            objectMapperWithRootName.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            objectMapperWithRootName.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
            objectMapperWithRootName.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
        }

        if (prettyPrintOption) {
            return objectMapperWithRootName.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } else {
            return objectMapperWithRootName.writeValueAsString(object);
        }
    }

    public static void objectToJasonWithOutputStream(OutputStream outputStream, Object object, boolean prettyPrintOption) throws JsonGenerationException, JsonMappingException, IOException {
        if (prettyPrintOption) {
            SpringUtil.getObjectMapper().writerWithDefaultPrettyPrinter().writeValue(outputStream, object);
        } else {
            SpringUtil.getObjectMapper().writeValue(outputStream, object);
        }
    }

    public static Map<String, Object> jsonToMap(String json) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, new TypeReference<HashMap<String, Object>>() {});
    }

    public static <T> T jsonToClass(String json, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, clazz);
    }

    public static <T> T jsonToClass(String json, TypeReference<T> type) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, type);
    }

    public static <T> List<T> findMatchingOrigins(List<T> origins, List<T> myThings, Function<T, ?> keyExtractor) {
        List<?> myThingKyes = Optional.ofNullable(myThings).orElseGet(Collections::emptyList).stream()
                .map(keyExtractor)
                .toList();

        return origins.stream()
                .filter(origin -> myThingKyes.contains(keyExtractor.apply(origin)))
                .collect(Collectors.toList());
    }

    public static Set<String> enumerationToSet(Enumeration<String> enumeration) {
        Set<String> set = new HashSet<>();
        while (enumeration.hasMoreElements()) {
            set.add(enumeration.nextElement());
        }
        return set;
    }

    public static Set<String> collectionToSet(Collection<String> collection) {
        Set<String> set = new HashSet<>(collection);
        return set;
    }

    public static Enumeration<String> collectionToEnumeration(Collection<String> collection) {
        return Collections.enumeration(collection);
    }

    public static List<String> enumerationToList(Enumeration<String> enumeration) {
        List<String> list = new ArrayList<>();
        while (enumeration.hasMoreElements()) {
            list.add(enumeration.nextElement());
        }
        return list;
    }

    public static HttpHeaders objMapToHttpHeaders(Map<String, Object> headerMap) {
        HttpHeaders httpHeaders = new HttpHeaders();

        for (Map.Entry<String, Object> entry : headerMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof String) {
                httpHeaders.add(key, (String) value);
            } else if (value instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> list = (List<String>) value;
                httpHeaders.addAll(key, list);
            } else if (value instanceof Set) {
                @SuppressWarnings("unchecked")
                Set<String> set = (Set<String>) value;
                httpHeaders.addAll(key, new ArrayList<>(set));
            } else {
                throw new IllegalArgumentException("Unsupported header value type for key: " + key);
            }
        }

        return httpHeaders;
    }
}

