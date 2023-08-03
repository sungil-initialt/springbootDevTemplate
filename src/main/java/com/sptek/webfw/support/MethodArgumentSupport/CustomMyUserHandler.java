package com.sptek.webfw.support.MethodArgumentSupport;

import lombok.Getter;
import lombok.Setter;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class CustomMyUserHandler implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        //해당 클레스와 정확히 일치하는 경우만 적용
        //return methodParameter.getParameterType().equals(MyUser.class);

        //해당 클레스를 상속받은 클레스까지 포함 가능
        return methodParameter.getParameterType().isAssignableFrom(MyUser.class)
                //@CustomArgument 어노테이션과 함께 사용했을때만 유효하게 처리 가능
                && methodParameter.hasParameterAnnotation(CustomArgument.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer
            , NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {

        MyUser myUser = new MyUser();
        myUser.setId("sungilry");
        myUser.setName("이성일");
        myUser.setType(MyUser.UserType.customer);

        return myUser;
    }

    @Getter
    @Setter
    public class MyUser {
        private String id;
        private String name;
        private UserType type;

        public enum UserType {
            customer, manager, admin
        }
    }
}
