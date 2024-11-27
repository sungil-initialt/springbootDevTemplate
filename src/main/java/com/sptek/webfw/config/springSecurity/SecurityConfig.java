package com.sptek.webfw.config.springSecurity;

import com.sptek.webfw.config.springSecurity.spt.CustomAuthenticationProvider;
import com.sptek.webfw.util.SecureUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomJwtAuthenticationEntryPoint customJwtAuthenticationEntryPoint;
    private final CustomLoginSuccessHandler customLoginSuccessHandler;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    private final CustomJwtAccessDeniedHandler customJwtAccessDeniedHandler;
    private final GeneralTokenProvider generalTokenProvider;

    @Bean
    //로그인 전체 스템을 관리할 AuthenticationManager(=ProviderManager)에 AuthenticationProvider을 추가하여 반환. (필요에 따라 만들어진 AuthenticationProvider)
    public AuthenticationManager authManager(HttpSecurity httpSecurity) throws Exception {
        //AuthenticationProvider 가 여러개 설정된 상황에 대해서 어떤 전략?으로 처리할지 커스텀이 필요하다면
        //AuthenticationManager 에 대한 custom 작업이 필요함!

        AuthenticationManagerBuilder authenticationManagerBuilder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(new CustomAuthenticationProvider());
        return authenticationManagerBuilder.build();
    }

    @Bean
    // 주로 SecurityFilterChain 에서 특정 경로(js, css resource 경로등)를 제외하는 용도로 사용 (6.x 버전부터 아래 SecurityFilterChain에서도 처리됨으로 의미가 별로 없어짐)
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (webSecurity) -> webSecurity.ignoring()
                .requestMatchers(SecureUtil.getNotEssentialRequestPatternsArray())
                .requestMatchers("/api/v1/hello");
        //All processing by Spring Security is bypassed.(This is not recommended)
        //return (webSecurity) -> webSecurity.ignoring().requestMatchers("/**");
    }

    @Bean
    //비밀번호 암복호용
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    //스프링 6.x 버전부터 변경된 방식으로, spring security는 자체적으로 준비된 필터들과 동작 순서가 있으며 아래는 그 필터들의 동작유무 및 설정 옵션을 지정하는 역할을 한다.
    public SecurityFilterChain securityFilterChainForWeb(HttpSecurity httpSecurity) throws Exception {
        customLoginSuccessHandler.setDefaultTargetUrl("/"); // --> 여기처리
        
        httpSecurity
                //path별 Role을 지정함
                .authorizeHttpRequests(authorize ->
                    authorize
                            .requestMatchers("/","/signup", "/signin", "/login", "/logout", "/signout").permitAll()  //기본으로 오픈할 경로
                            .requestMatchers("/my/**", "/mypage/**").hasAnyRole("USER", "ADMIN", "ADMIN_MARKETING", "SYSTEM")
                            .requestMatchers("/admin/**").hasAnyRole("ADMIN", "SYSTEM")
                            .requestMatchers("/system/**").hasAnyRole("SYSTEM")
                            .anyRequest().permitAll() //그외
                            //.anyRequest().authenticated() //그외
                )

                //인증과 관련된 EX 처리 주체 설정
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                //인증 오류를 케치하여 처리(todo : 비설정시 login 페이지로 이동됨, 반대로 설정되면 로그인 페이지로 자동 연결되지 않음, 필터의 순서상의 이유가 있는 듯, 자세한건 확인 필요)
                                //.authenticationEntryPoint(jwtAuthenticationEntryPoint)
                                .accessDeniedHandler(customJwtAccessDeniedHandler) //인가 오류 진입점
                )

                //.httpBasic(Customizer.withDefaults()) //얼럿창형
                //.formLogin(withDefaults()) //form형 디폴트 로그인 (--:8443/login 으로 고정되어 있는듯 8443 포트에서만 정상 동작됨)
                .formLogin(form -> form
                        .loginPage("/signin") //custom 로그인 폼
                        .successHandler(customLoginSuccessHandler)
                        .failureHandler(customAuthenticationFailureHandler)
                        
                )


                // 로그아웃 처리
                // 1. SecurityContext에 저장된 인증 정보 제거
                // 2. 기본적으로 JSESSIONID 쿠키를 삭제
                // 3. /login?logout 으로 리다이렉트 처리 (logoutSuccessHandler 추가시에는 logoutSuccessHandler 에서 해줘야함)
                .logout(logout -> logout
                        // 로그아웃 처리 url 설정 (해당 req 매핑이 존재할 필요는 없음)
                        .logoutUrl("/logout")

                        //추가적인 로직이 필요한 경우
                        /*
                        .logoutSuccessHandler((request, response, authentication) -> {
                            log.debug("User has logged out: " + (authentication != null ? authentication.getName() : "Anonymous"));
                            response.sendRedirect("/signin?logout"); //마지막 리다이렉트 처리를 직접 해줘야함
                        })
                        */
                );

        return httpSecurity.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChainForApi(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // JWT를 사용하는 경우 CSRF방지 기능을 사용 할 필요가 없음.(CSRF 공격이 session 쿠키 방식의 문제 점에 기인한 것으로 JWT만 사용하여 세션을 사용하지 않는다면 disable 처리)
                .csrf(csrf ->
                        csrf.disable())

                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers("/api/**/signup", "/api/**/signin").permitAll()
                                .requestMatchers("/api/**/admin/marketing/**").hasAnyRole("ADMIN_MARKETING", "SYSTEM")
                                .requestMatchers("/api/**/my/**", "/api/**/mypage/**").hasAnyRole("USER", "ADMIN_DEFAULT", "SYSTEM")
                                .requestMatchers("/api/**/system/**").hasAnyRole("SYSTEM")
                                .anyRequest().permitAll()
                )

                .exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                .authenticationEntryPoint(customJwtAuthenticationEntryPoint)
                                .accessDeniedHandler(customJwtAccessDeniedHandler) //인가 오류 진입점
                )

                //security와 관련해서 custom하게 만든 필터가 있다면 적정 위치에 추가할 수 있다.
                //(UsernamePasswordAuthenticationFilter 은 스프링 자체 필터로, post 방식, /login 경로 요청시 동작하며 해당 req post로 전달된 정보를 이용해 스프링의 authenticationManager 통한 인증 절차를 요청함)
                .addFilterBefore(new CustomJwtFilter(generalTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
}
