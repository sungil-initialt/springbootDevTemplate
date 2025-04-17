package com.sptek._frameworkWebCore.external;

import com.sptek._frameworkWebCore.annotation.EnableThymeleafSpringSecurityDialect_InMain;
import com.sptek._frameworkWebCore.annotation.annotationCondition.HasAnnotationOnMain_InBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;


@Configuration //Thymeleaf 관련 Bean 의 설정
public class ThymeleafConfig {

    @HasAnnotationOnMain_InBean(EnableThymeleafSpringSecurityDialect_InMain.class)
    @Bean //Thymeleaf 에서 spring-security 요소를 사용하기 위함
    public SpringSecurityDialect springSecurityDialect() {
        return new SpringSecurityDialect();
    }
}

/*
<div sec:authorize="isAuthenticated()">
    로그인한 사용자만 볼 수 있는 영역
</div>

<div sec:authorize="hasRole('ADMIN')">
    관리자만 볼 수 있는 영역
</div>

<p>안녕하세요, <span sec:authentication="name"></span> 님!</p>
*/
