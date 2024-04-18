package com.sptek.webfw.config.springSecurity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {
    //private final TokenProvider tokenProvider;
    private final CustomAuthenticationProvider customAuthenticationProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    //인증 방식을 구현한 AuthenticationProvider의 impl 를 ProviderManager(AuthenticationManager의 impl)에 등록
    //WebSecurityConfigurerAdapter 가 deprecated 되면서 방식이 변경됨
    public AuthenticationManager authManager(HttpSecurity httpSecurity) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(customAuthenticationProvider);
        return authenticationManagerBuilder.build();
    }

    @Bean
    //All processing by Spring Security is bypassed.(This is not recommended)
    public WebSecurityCustomizer webSecurityCustomizer() {
        //return (webSecurity) -> webSecurity.ignoring().requestMatchers(SecureUtil.getNotEssentialRequestPatternsArray());
        return (webSecurity) -> webSecurity.ignoring().requestMatchers("/**");
    }



    /*
    //-->여기 수정해야 함
    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf().disable()
                .authorizeHttpRequests(authz ->
                        authz.requestMatchers(SecureUtil.getNotEssentialRequestPatternsArray()).permitAll()
                )
                .authorizeHttpRequests(authz -> {
                        try {
                            authz
                                    .requestMatchers("/api/**").authenticated()
                                    .and()
                                    .exceptionHandling()
                                    .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                                    .accessDeniedHandler(jwtAccessDeniedHandler);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                )
                .authorizeHttpRequests(authz -> {
                        try {
                            authz
                                    .requestMatchers("/login", "/signup").permitAll()
                                    .requestMatchers("/admin").hasRole("ADMIN")
                                    .requestMatchers("/my").authenticated()
                                    .and()
                                    //.formLogin()
                                    //.loginPage("/login")
                                    //.successHandler(customLoginSuccessHandler)
                                    //.failureForwardUrl("/fail")
                                    .formLogin(Customizer.withDefaults())
                                    .exceptionHandling()
                                    .accessDeniedHandler(jwtAccessDeniedHandler)
                                    .and()
                                    .logout()
                                    .logoutUrl("/logout");
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                );
        return httpSecurity.build();
    }
    */
}
