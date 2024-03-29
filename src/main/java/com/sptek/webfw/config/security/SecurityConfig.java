package com.sptek.webfw.config.security;

import com.sptek.webfw.util.SecureUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@Configuration
public class SecurityConfig {

    /*
    //All processing by Spring Security is bypassed.(This is not recommended)
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (webSecurity) -> webSecurity.ignoring().requestMatchers(SecureUtil.getNotEssentialRequestPatternsArray());
    }
    */
    
    //-->여기 수정해야 함
    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf().disable()
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(SecureUtil.getNotEssentialRequestPatternsArray()).permitAll()
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

}
