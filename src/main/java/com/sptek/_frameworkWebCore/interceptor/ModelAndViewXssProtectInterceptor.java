package com.sptek._frameworkWebCore.interceptor;

import com.sptek._frameworkWebCore.annotation.TestAnnotation_InAll;
import com.sptek._frameworkWebCore.annotation.annotationCondition.HasAnnotationOnMain_InBean;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@HasAnnotationOnMain_InBean(TestAnnotation_InAll.class)
@Slf4j
@Component

public class ModelAndViewXssProtectInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull Object handler,
            ModelAndView modelAndView
    ) {
        if (handler instanceof HandlerMethod && modelAndView != null) {
            log.debug("🛡️ Interceptor: XSS escaping ModelAndView");
            Map<String, Object> model = modelAndView.getModel();
            model.replaceAll((key, value) -> escapeIfNeeded(value));
        }
    }

    private Object escapeIfNeeded(Object value) {
        if (value instanceof String str) {
            return StringEscapeUtils.escapeHtml4(str);
        }

        if (value instanceof List<?> list) {
            return list.stream().map(this::escapeIfNeeded).collect(Collectors.toList());
        }

        if (value instanceof Map<?, ?> map) {
            return map.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> escapeIfNeeded(e.getValue()),
                            (a, b) -> b,
                            LinkedHashMap::new
                    ));
        }

        if (isDto(value)) {
            return escapeDto(value);
        }

        return value;
    }

    private boolean isDto(Object obj) {
        return obj != null
                && !(obj instanceof Enum)
                && obj.getClass().getPackageName().startsWith("com.sptek.");
    }

    private Object escapeDto(Object dto) {
        for (Field field : getAllFields(dto.getClass())) {
            field.setAccessible(true);
            try {
                Object fieldValue = field.get(dto);
                if (fieldValue instanceof String str) {
                    field.set(dto, StringEscapeUtils.escapeHtml4(str));
                } else if (fieldValue instanceof List<?> || fieldValue instanceof Map<?, ?> || isDto(fieldValue)) {
                    field.set(dto, escapeIfNeeded(fieldValue));
                }
            } catch (Exception e) {
                log.warn("XSS 필터링 중 DTO 처리 실패 - 클래스: {}, 필드: {}", dto.getClass().getName(), field.getName(), e);
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