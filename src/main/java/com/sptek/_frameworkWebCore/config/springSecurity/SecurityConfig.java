package com.sptek._frameworkWebCore.config.springSecurity;

import com.sptek._frameworkWebCore.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) //application  메소드에서도 접근제어 가능하게 함
public class SecurityConfig {

    @Bean
    //로그인 전체 스템을 관리할 AuthenticationManager(=ProviderManager)에 AuthenticationProvider을 추가하여 반환. (필요에 따라 만들어진 AuthenticationProvider)
    public AuthenticationManager authManager(HttpSecurity httpSecurity) throws Exception {
        //AuthenticationProvider 가 여러개 설정된 상황에 대해서 어떤 전략?으로 처리할지 커스텀이 필요하다면
        //AuthenticationManager 에 대한 custom 작업이 필요함!

        AuthenticationManagerBuilder authenticationManagerBuilder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        //customAuthenticationProvider가 @Component로 구성되서 그런지.. 자동으로 감지해서 설정이 되는듯 함(이코드를 넣으면 프로바이드가 두번 설정됨)
        //authenticationManagerBuilder.authenticationProvider(customAuthenticationProvider);
        authenticationManagerBuilder.eraseCredentials(true);
        return authenticationManagerBuilder.build();
    }

    @Bean
    // 주로 SecurityFilterChain 에서 특정 경로(js, css resource 경로등)를 제외하는 용도로 사용
    // 아래 securityFilterChain에서 도 유사하게 처리 할수 있으나.. 이곳에 설정한 경로는 spring-security 와 관련한 모든 설정이 적용되지 않음 (다른 필터에서 security 관련 사용지 주위 필요)
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (webSecurity) -> webSecurity.ignoring()
                .requestMatchers(SecurityUtil.getNotEssentialRequestPatternsArray())
                .requestMatchers("/api/v1/hello");
        //All processing by Spring Security is bypassed.(This is not recommended)
        //return (webSecurity) -> webSecurity.ignoring().requestMatchers("/**");
    }
}
