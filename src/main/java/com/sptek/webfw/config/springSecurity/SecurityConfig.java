package com.sptek.webfw.config.springSecurity;

import com.sptek.webfw.util.SecureUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
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

    // 주로 SecurityFilterChain 에서 특정 경로(js, css resource 경로등)를 제외하는 용도로 사용 (6.x 버전부터 SecurityFilterChain에서 처리가능해서 의미가 별로 없어짐)
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (webSecurity) -> webSecurity.ignoring()
                .requestMatchers(SecureUtil.getNotEssentialRequestPatternsArray())
                .requestMatchers("/api/v1/hello");

        //All processing by Spring Security is bypassed.(This is not recommended)
        //return (webSecurity) -> webSecurity.ignoring().requestMatchers("/**");
    }

    //-->여기 수정해야 함
    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrf ->
                    csrf.disable()) // JWT를 사용하는 경우 CSRF방지 기능을 사용 할 필요가 없음.(CSRF 공격이 session 쿠키 방식의 문제 점에 기인한 것으로 JWT는 세션이 기반이 아니기 때문에)

                //Request Matchers
                .authorizeHttpRequests(authorize ->
                    authorize
                            .requestMatchers("/signup", "/login", "/api/auth").permitAll()  //인증 처리를 위한 오픈 경로
                            .requestMatchers("/admin/**", "/api/admin/**").hasRole("ADMIN")
                            .requestMatchers("/my/**", "/mypage/**", "/api/**").hasAnyRole("ADMIN", "USER")
                            .anyRequest().permitAll() //그외
                        //.anyRequest().authenticated() //그외
                )

                //로그인 페이지로 httpBasic 또는 formLogin을 사용하려면 exceptionHandling을 설정하지 말아야 함(설정시 핸들러에서 먼저 처리됨)
                //.httpBasic(Customizer.withDefaults()) //얼럿창 형식의 id/pw 입력 제공
                //.formLogin(withDefaults()) //form 형식의 id/pw 입력 제공, :8443/login 으로 고정됨 (property 설정을 8443으로 해야함)

                .exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                //.authenticationEntryPoint(jwtAuthenticationEntryPoint) //spring security 관련 처리중 EX가 발생 했을 때 최초 진입점.
                                .accessDeniedHandler(jwtAccessDeniedHandler)

                );


                //.formLogin(form -> form.loginPage("https://front.localhost:8080/login"));

                /*
                .authorizeHttpRequests(authz ->
                        authz.requestMatchers("/login", "/signup").permitAll()
                                .requestMatchers("/admin").hasRole("ADMIN")
                                .requestMatchers("/my").authenticated())

                .for
                                    .loginPage("/login")
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

                 */
        return httpSecurity.build();
    }
}
