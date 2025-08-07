package com.sptek._projectCommon.argumentResolver;

import com.sptek._frameworkWebCore._example.dto.ExUserDto;
import com.sptek._frameworkWebCore._annotation.Enable_ArgumentResolver_At_Param;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.io.IOException;
import java.util.Optional;

@Component
public class ExampleArgumentResolverForExUserDto implements HandlerMethodArgumentResolver {
    //Controller 에만 적용 가능함

    @Override //적용 조건 설정
    public boolean supportsParameter(MethodParameter methodParameter) {
        //해당 클레스와 정확히 일치하는 경우만 적용할때
        //return methodParameter.getParameterType().equals(MyUser.class);

        //해당 클레스를 상속받은 클레스까지 적용할때
        //return methodParameter.getParameterType().isAssignableFrom(MyUserDto.class)

        //해당 클레스를 상속받은 클레스까지 적용하지만 rgumentResolver를 적용하겠다는 별도의 어노테이션이 있는 경우만 처리
        return methodParameter.getParameterType().isAssignableFrom(ExUserDto.class)
                && methodParameter.hasParameterAnnotation(Enable_ArgumentResolver_At_Param.class);
    }


    @Override
    //MethodParameter, modelAndViewContainer, nativeWebRequest, webDataBinderFactory 을 응용 해서 더 많은 곳에 활용 가능.
    public Object resolveArgument(@NotNull MethodParameter methodParameter
            , ModelAndViewContainer modelAndViewContainer
            , NativeWebRequest nativeWebRequest
            , WebDataBinderFactory webDataBinderFactory) throws IOException {

        ExUserDto exUserDto;
        String id = Optional.ofNullable(nativeWebRequest.getParameter("id")).map(String::toString).orElse("anonymous");

//        if(id.equals("anonymous")) {
//            String body = nativeWebRequest.getNativeRequest(HttpServletRequest.class).getReader().lines().collect(Collectors.joining(System.lineSeparator()));
//            id = new ObjectMapper().readValue(body, ExUserDto.class).getId();
//        }

        if(id.equals("sungilry")) {
            exUserDto = ExUserDto.builder()
                    .id(id)
                    .name("SPT " + id + " 님으로 변경 했어요!")
                    .type(ExUserDto.UserType.admin)
                    .build();
        } else {
            exUserDto = ExUserDto.builder()
                    .id(id)
                    .name(id + " 님")
                    .type(ExUserDto.UserType.anonymous)
                    .build();
        }

        return exUserDto;
    }
}
