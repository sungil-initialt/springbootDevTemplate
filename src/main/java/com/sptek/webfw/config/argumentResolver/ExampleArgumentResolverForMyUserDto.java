package com.sptek.webfw.config.argumentResolver;

import com.sptek.webfw.anotation.AnoCustomArgument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Optional;

public class ExampleArgumentResolverForMyUserDto implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        //해당 클레스와 정확히 일치하는 경우만 적용
        //return methodParameter.getParameterType().equals(MyUser.class);

        //해당 클레스를 상속받은 클레스까지 포함 가능
        return methodParameter.getParameterType().isAssignableFrom(MyUserDto.class)
                && methodParameter.hasParameterAnnotation(AnoCustomArgument.class); //@CustomArgument 어노테이션과 함께 사용했을때만 유효하게 처리하겠다는 설정.
    }


    @Override
    //controller에 obj를 넘기면서 특정한 처리를 미리 해서 넘길수 있게 해준다(반복적으로 이루어지는 작업에 활용하면 유용)
    public Object resolveArgument(@NotNull MethodParameter methodParameter
            , ModelAndViewContainer modelAndViewContainer
            , NativeWebRequest nativeWebRequest
            , WebDataBinderFactory webDataBinderFactory) {

        MyUserDto myUserDto;
        String id = Optional.ofNullable(nativeWebRequest.getParameter("id")).map(String::toString).orElse("anonymous");
        if(id.equals("admin")) {
            myUserDto = MyUserDto.builder()
                    .id(id)
                    .name("관리자")
                    .type(MyUserDto.UserType.admin)
                    .build();
        } else {
            myUserDto = MyUserDto.builder()
                    .id(id)
                    .name(id + " 님")
                    .type(MyUserDto.UserType.anonymous)
                    .build();
        }

        //MethodParameter, modelAndViewContainer, nativeWebRequest, webDataBinderFactory을 응용해서 더 많은곳에 활용.

        return myUserDto;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    //해당 클레스가 inner class로 있을 필요는 없음(그냥 편의상 한것임)
    public static class MyUserDto {
        private String id;
        private String name;
        private UserType type;

        public enum UserType {
            customer, manager, admin, anonymous
        }
    }
}
