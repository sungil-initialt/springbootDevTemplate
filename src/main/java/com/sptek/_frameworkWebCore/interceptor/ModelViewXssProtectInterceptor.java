package com.sptek._frameworkWebCore.interceptor;

import com.sptek._frameworkWebCore.annotation.Enable_XssProtectorForView_At_ControllerMethod;
import com.sptek._frameworkWebCore.base.constant.CommonConstants;
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

//@HasAnnotationOnMain_InBean(TestAnnotation_InAll.class) //HasAnnotationOnMain 설정 으로 처리 하려다 성능을 고려 하여 controller Annotation 적용 으로 변경함
@Slf4j
@Component

public class ModelViewXssProtectInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull Object handler,
            ModelAndView modelAndView
    ) {
        if (handler instanceof HandlerMethod handlerMethod && modelAndView != null) {
            if (handlerMethod.hasMethodAnnotation(Enable_XssProtectorForView_At_ControllerMethod.class)) {
                log.debug("ModelView Xss Protector On");
                Map<String, Object> model = modelAndView.getModel();
                model.replaceAll((key, value) -> escapeIfNeeded(value));
            }
        }
    }

    private Object escapeIfNeeded(Object value) {
        if (value instanceof String str) {
            return StringEscapeUtils.escapeHtml4(str);
        }

        if (value instanceof List<?> list) {
            return list.stream().map(this::escapeIfNeeded).collect(Collectors.toList());
        }

        if (value instanceof Set<?> set) {
            return set.stream().map(this::escapeIfNeeded).collect(Collectors.toSet());
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

        if (value instanceof Object[] array) {
            return Arrays.stream(array).map(this::escapeIfNeeded).toArray();
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
                    field.set(dto, escapeIfNeeded(fieldValue));
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