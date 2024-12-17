package com.sptek.webfw.config.springSecurity;

import com.sptek.webfw.util.SecureUtil;
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
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final CustomJwtAuthenticationEntryPointForApi customJwtAuthenticationEntryPointForApi;
    private final CustomAuthenticationSuccessHandlerForView customAuthenticationSuccessHandlerForView;
    private final CustomAuthenticationFailureHandlerForView customAuthenticationFailureHandlerForView;
    private final CustomJwtAccessDeniedHandlerForApi customJwtAccessDeniedHandlerForApi;
    private final GeneralTokenProvider generalTokenProvider;
    //private final CustomAuthenticationProvider customAuthenticationProvider;

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
    // 주로 SecurityFilterChain 에서 특정 경로(js, css resource 경로등)를 제외하는 용도로 사용 (6.x 버전부터 아래 SecurityFilterChain에서도 처리됨으로 의미가 별로 없어짐)
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (webSecurity) -> webSecurity.ignoring()
                .requestMatchers(SecureUtil.getNotEssentialRequestPatternsArray())
                .requestMatchers("/api/v1/hello");
        //All processing by Spring Security is bypassed.(This is not recommended)
        //return (webSecurity) -> webSecurity.ignoring().requestMatchers("/**");
    }

    @Bean
    //스프링 6.x 버전부터 변경된 방식으로, spring security는 자체적으로 준비된 필터들과 동작 순서가 있으며 아래는 그 필터들의 동작유무 및 설정 옵션을 지정하는 역할을 한다.
    public SecurityFilterChain securityFilterChainForWeb(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // api/** 로 시작하지 않은 경우만 필터 적용
                .securityMatcher(request -> !request.getRequestURI().startsWith("/api"))

                //path별 Role을 지정함 (controller 의  @PreAuthorize와의 차이점은 여기서 path에 지정하는 방식은 spring security fillter 에 의해 관리되고.. controller 에 지정된 것은 servlet 에서 관리됨)
                //다시말해.. path에 지정하면.. 인증이 필요할때 spring-security-fillter가 로그인 페이지로 자동 이동해 주거나.. 권한이 없을때 filter 레벨에서 403 페이지로 전환해 준다.
                //@PreAuthorize 방식은 인증이 필요한 경우나 권한이 없는경우 관련 EX가 발생되고.. 그에 따른 처리는 Sevlet 내에서 개발자가 알아서 처리해 주어야 한다.(로그인페이지로 자동 연결해주는거 없음)
                //1차적으로 Role에 따라 path를 구분하여 권한처리를 하고 특정 페이지에 조회 기능과 수정 기능이 각각의 별도 권한이 필요하다면 이런 경우는 컨트롤러에 권한 설정을 하는 방식이 적합하지 않을까..
                .authorizeHttpRequests(authorize ->
                    authorize //-->fillter 방식과 @PreAuthorize 방식의 선택 기준 고민 필요
                            .requestMatchers("/","/signup", "/login", "/logout").permitAll()
                            .requestMatchers("/auth/**", "/my/**", "/mypage/**").authenticated() //권한은 필요하지만 특정 Role로 지정이 어려울때
                            .requestMatchers("/public/anyone/butNeedFilterAuth").hasAuthority(AuthorityIfEnum.AUTH_SPECIAL_FOR_TEST.name()) //필터에서 특정 authority를 직접 확인하는 케이스(비로그인시 로그인페이지로, 로그인되어 있으나 권한이 없을때는 403 페이지로 이동)
                            .requestMatchers("/user/**").hasAnyRole("USER")
                            .requestMatchers("/admin/**").hasAnyRole("ADMIN", "ADMIN_SPECIAL")
                            .requestMatchers("/system/**").hasAnyRole("SYSTEM")
                            //.requestMatchers("/public/anyone/butNeedControllRole").hasAuthority(AuthorityIfEnum.AUTH_SPECIAL_FOR_TEST.name()) //여기서 걸리면 403 에러페이지를 직접 찾아감
                            .anyRequest().permitAll() //그외
                            //.anyRequest().authenticated() //그외
                )

                //.httpBasic(Customizer.withDefaults()) //얼럿창형
                //.formLogin(withDefaults()) //form형 디폴트 로그인 (--:8443/login 으로 고정되어 있는듯 8443 포트에서만 정상 동작됨)
                .formLogin(form -> form
                        .loginPage("/login")
                        //.defaultSuccessUrl("/")
                        .successHandler(customAuthenticationSuccessHandlerForView)
                        .failureHandler(customAuthenticationFailureHandlerForView)
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
                            response.sendRedirect("/login?logout"); // custom 코드를 넣었다면 마지막 리다이렉션 처리까지 직접 해줘야함.
                        })
                        */
                );

        return httpSecurity.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChainForApi(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // api/** 에 적용되는 경우만 필터 적용
                .securityMatcher("/api/**")

                // JWT를 사용하는 경우 CSRF방지 기능을 사용 할 필요가 없음.(CSRF 공격이 session 쿠키 방식의 문제 점에 기인한 것으로 JWT만 사용하여 세션을 사용하지 않는다면 disable 처리)
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers("/api/*/signup", "/api/*/login", "/api/*/logout").permitAll()
                                .requestMatchers("/api/*/auth/**", "/api/*/my/**", "/api/*/mypage/**").authenticated() //권한은 필요하지만 특정 Role로 지정이 어려울때
                                .requestMatchers("/api/*/public/anyone/butNeedFilterAuth").hasAuthority(AuthorityIfEnum.AUTH_SPECIAL_FOR_TEST.name()) //필터에서 특정 authority를 직접 확인하는 케이스
                                .requestMatchers("/api/*/user/**").hasAnyRole("USER")
                                .requestMatchers("/api/*/admin/**").hasAnyRole("ADMIN")
                                .requestMatchers("/api/*/system/**").hasAnyRole("SYSTEM")
                                .anyRequest().permitAll()
                )

                // API 에러 플로우가 그데로 적용되도록 수정 필요 (여기부터!!)
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                .authenticationEntryPoint(customJwtAuthenticationEntryPointForApi) //인증 오류 진입점
                                .accessDeniedHandler(customJwtAccessDeniedHandlerForApi) //인가 오류 진입점
                )

                //security와 관련해서 custom하게 만든 필터가 있다면 적정 위치에 추가할 수 있다.
                //UsernamePasswordAuthenticationFilter 은 스프링 자체 필터로, post 방식, /login 경로 요청시 동작하며 해당 POST request로 전달된 정보를 이용해 스프링의 authenticationManager 통한 인증 절차를 요청함
                .addFilterBefore(new CustomJwtFilter(generalTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
}
