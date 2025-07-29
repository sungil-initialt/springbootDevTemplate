package com.sptek._frameworkWebCore.support;

import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component

public class XssEscapeSupport {

    public Object escape(Object value) {
        if (value == null) return null;

        if (value instanceof String str) {
            return StringEscapeUtils.escapeHtml4(str);
        }

        if (value instanceof List<?> list) {
            return list.stream().map(this::escape).collect(Collectors.toList());
        }

        if (value instanceof Set<?> set) {
            return set.stream().map(this::escape).collect(Collectors.toSet());
        }

        if (value instanceof Map<?, ?> map) {
            return map.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> escape(e.getValue()),
                            (a, b) -> b,
                            LinkedHashMap::new
                    ));
        }

        if (value instanceof Object[] array) {
            return Arrays.stream(array).map(this::escape).toArray();
        }

        if (isMyDtoObject(value)) {
            return escapeDtoObject(value);
        }

        return value;
    }

    private boolean isMyDtoObject(Object obj) {
        return obj != null
                && !(obj instanceof Enum)
                && obj.getClass().getPackageName().startsWith(CommonConstants.PROJECT_PACKAGE_NAME);
    }

    private Object escapeDtoObject(Object dto) {
        for (Field field : getAllFields(dto.getClass())) {
            field.setAccessible(true);
            try {
                Object fieldValue = field.get(dto);
                if (fieldValue instanceof String str) {
                    field.set(dto, StringEscapeUtils.escapeHtml4(str));
                } else if (fieldValue instanceof List<?> || fieldValue instanceof Map<?, ?> || isMyDtoObject(fieldValue)) {
                    field.set(dto, escape(fieldValue));
                }
            } catch (Exception e) {
                log.warn("Xss Protecting Fail - class: {}, field: {}", dto.getClass().getName(), field.getName(), e);
            }
        }
        return dto;
    }

    private List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        while (type != null && type != Object.class) {
            fields.addAll(Arrays.asList(type.getDeclaredFields()));
            type = type.getSuperclass();
        }
        return fields;
    }
}
