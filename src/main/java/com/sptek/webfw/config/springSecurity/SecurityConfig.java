package com.sptek.webfw.config.springSecurity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Slf4j
@Configuration
public class SecurityConfig {
    //private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    public SecurityConfig(/*TokenProvider tokenProvider,*/ JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint, JwtAccessDeniedHandler jwtAccessDeniedHandler) {
        //this.tokenProvider = tokenProvider;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //All processing by Spring Security is bypassed.(This is not recommended)

    @Bean
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
