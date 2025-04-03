package com.sptek._projectCommon.springSecurity;

import com.sptek._frameworkWebCore.springSecurity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) //application  메소드에서도 접근제어 가능하게 함
public class SecurityFilterChainConfig {

    private final CustomAuthenticationSuccessHandlerForView customAuthenticationSuccessHandlerForView;
    private final CustomAuthenticationFailureHandlerForView customAuthenticationFailureHandlerForView;
    private final GeneralTokenProvider generalTokenProvider;

    //private final CustomJwtAuthenticationEntryPointForApi customJwtAuthenticationEntryPointForApi;
    //private final CustomJwtAccessDeniedHandlerForApi customJwtAccessDeniedHandlerForApi;
    //private final CustomAuthenticationProvider customAuthenticationProvider;

    @Bean
    public SecurityFilterChain h2ConsoleSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                //H2 DB 웹 콘설 전용
                .securityMatcher("/h2-console/**")
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers
                        //콘솔 UI 구성상 FrameOptionsConfig::disable 옵션이 필요한데 보안상 해당 경로만 적용
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                )
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );

        return http.build();
    }

    @Bean
    //스프링 6.x 버전부터 변경된 방식으로, spring security는 자체적으로 준비된 필터들과 동작 순서가 있으며 아래는 그 필터들의 동작유무 및 설정 옵션을 지정하는 역할을 한다.
    public SecurityFilterChain securityFilterChainForView(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // api/** 로 시작하지 않은 경우만 필터 적용
                .securityMatcher(request -> !request.getRequestURI().startsWith("/api/") && !request.getRequestURI().startsWith("/h2-console/"))

                // CSRF를 비활성화할 경로 지정
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/**") // todo: 테스트를 편하게 하기 위해 모든 경로에서 dsrf 토큰을 무시하도록 임시 처리
                        .ignoringRequestMatchers("/example/**")
                        .ignoringRequestMatchers("/public/**")
                )

                // todo: session 과 관련된 전반적인 부분을 확인해야함!!!
                // .sessionManagement(session -> session
                //        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // 세션이 필요한 경우에만 생성
                //        .invalidSessionUrl("/session-invalid") // 세션이 유효하지 않으면 이동할 URL 설정
                //        .maximumSessions(1) // 최대 세션 수 설정
                //        .maxSessionsPreventsLogin(true) // 세션 수가 초과되면 로그인 방지
                //        .expiredUrl("/session-expired") //)
                // )

                //path별 Role을 지정함 (controller 의  @PreAuthorize와의 차이점은 여기서 path에 지정하는 방식은 spring security fillter 에 의해 관리되고.. controller 에 지정된 것은 servlet 에서 관리됨)
                //다시말해.. path에 지정하면.. 인증이 필요할때 spring-security-fillter가 로그인 페이지로 자동 이동해 주거나.. 권한이 없을때 filter 레벨에서 403 페이지로 전환해 준다.
                //@PreAuthorize 방식은 인증이 필요한 경우나 권한이 없는경우 관련 EX가 발생되고.. 그에 따른 처리는 Sevlet 내에서 개발자가 알아서 처리해 주어야 한다.(로그인페이지로 자동 연결해주는거 없음)
                //1차적으로 Role에 따라 path를 구분하여 권한처리를 하고 특정 페이지에 조회 기능과 수정 기능이 각각의 별도 권한이 필요하다면 이런 경우는 컨트롤러에 권한 설정을 하는 방식이 적합하지 않을까..
                .authorizeHttpRequests(authorize ->
                    authorize //-->fillter 방식과 @PreAuthorize 방식의 선택 기준 고민 필요
                            .requestMatchers("/auth/**", "/my/**", "/mypage/**").authenticated() //권한은 필요하지만 특정 Role로 지정이 어려울때
                            .requestMatchers("/public/anyone/butNeedFilterAuth").hasAuthority(AuthorityIfEnum.AUTH_SPECIAL_FOR_TEST.name()) //필터에서 특정 authority를 직접 확인하는 케이스(비로그인시 로그인페이지로, 로그인되어 있으나 권한이 없을때는 403 페이지로 이동)
                            .requestMatchers("/user/**").hasAnyRole("USER")
                            .requestMatchers("/admin/**").hasAnyRole("ADMIN", "ADMIN_SPECIAL")
                            .requestMatchers("/system/**").hasAnyRole("SYSTEM")
                            //.requestMatchers("/public/anyone/butNeedControllRole").hasAuthority(AuthorityIfEnum.AUTH_SPECIAL_FOR_TEST.name()) //여기서 걸리면 403 에러페이지를 직접 찾아감
                            .requestMatchers("/","/signup", "/login", "/logout").permitAll()
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
                        // <View 로그인 처리 프로세서>
                        // 로그인 폼에서 정보 입력 후 <form action=/login> 으로 하면 서버의 AuthenticationManager 로 입력 정보가 자동 으로 전달됨
                        // 전달 받은 로그인 정보를 보고 어떤 타입의 로그인 처리 인지 확인후 등록된 AuthenticationProvider(CustomAuthenticationProvider)의 support 타입을 보고 그에 맞는 대상에 전달됨(security 내부적으로 처리됨)
                        // AuthenticationProvider(CustomAuthenticationProvider) 에서 관련 처리를 하고(개발자) 리턴 타입을 맟춰 응답하면 AuthenticationManager SecurityContextHolder 및 session 에 관련 처리를 함
                        // security 설정에 따라 다르지만 이후 요청이 들어오면 SecurityFilterChain 에소 세션(쿠키)을 기준으로 SecurityContextHolder 다시 가져와서 로그인 상태를 유지함
                        // 세션에 SecurityContextHolder가 없거나 만료되었다면.. 그에 따른 후속 처리 진행

                // 로그아웃 처리
                // 1. SecurityContext에 저장된 인증 정보 제거
                // 2. 기본적으로 JSESSIONID 쿠키를 삭제
                // 3. /login?logout 으로 리다이렉트 처리 (logoutSuccessHandler 추가시에는 logoutSuccessHandler 에서 해줘야함)
                .logout(logout -> logout
                        // 로그아웃 처리 url 설정 (해당 req 매핑이 존재할 필요는 없음)
                        .logoutUrl("/logout")

                        // 추가적인 로직이 필요한 경우
                        //.logoutSuccessHandler((request, response, authentication) -> {
                        //    log.debug("User has logged out: " + (authentication != null ? authentication.getName() : "Anonymous"));
                        //    response.sendRedirect("/login?logout"); // custom 코드를 넣었다면 마지막 리다이렉션 처리까지 직접 해줘야함.
                        //})

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
                .cors(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(authorize ->
                        authorize
                                //해당 요청의 post와 put은 인증해야 가능한 설정(컨트롤러에서 설정도 가능)
                                .requestMatchers(HttpMethod.POST,"/api/*/public/postPutAfterAuth").authenticated()
                                .requestMatchers(HttpMethod.PUT,"/api/*/public/postPutAfterAuth").authenticated()

                                .requestMatchers("/api/*/auth/**", "/api/*/my/**", "/api/*/mypage/**").authenticated() //권한은 필요하지만 특정 Role로 지정이 어려울때
                                .requestMatchers("/api/*/public/anyone/butNeedFilterAuth").hasAuthority(AuthorityIfEnum.AUTH_SPECIAL_FOR_TEST.name()) //필터에서 특정 authority를 직접 확인하는 케이스
                                .requestMatchers("/api/*/user/**").hasAnyRole("USER")
                                .requestMatchers("/api/*/admin/**").hasAnyRole("ADMIN")
                                .requestMatchers("/api/*/system/**").hasAnyRole("SYSTEM")
                                .requestMatchers("/api/*/signup", "/api/*/login", "/api/*/logout").permitAll()
                                .anyRequest().permitAll()
                )

                // CustomErrorController 를 이용해서 Controller 외부 에러(필터쪽이나.. 기타 등등) 상황에 대한 처리를 하고 있어서 사용할 필요가 없음 (Controller 에러 처리 흐름과 동일하게 처리되도록 함)
                //.exceptionHandling(exceptionHandling ->
                //        exceptionHandling
                //                .authenticationEntryPoint(customJwtAuthenticationEntryPointForApi) //인증 오류 진입점
                //                .accessDeniedHandler(customJwtAccessDeniedHandlerForApi) //인가 오류 진입점
                //)

                //security와 관련해서 custom하게 만든 필터가 있다면 적정 위치에 추가할 수 있다.
                //UsernamePasswordAuthenticationFilter 은 스프링 자체 필터로, post 방식, /login 경로 요청시 동작하며 해당 POST request로 전달된 정보를 이용해 스프링의 authenticationManager 통한 인증 절차를 요청함
                //api 방식일 경우 UsernamePasswordAuthenticationFilter 가 동작하면 안됨으로 그 앞에 CustomJwtFilter 두어 인증 관련 처리를 먼저 하도록 처리함
                .addFilterBefore(new CustomJwtFilter(generalTokenProvider), UsernamePasswordAuthenticationFilter.class);


        return httpSecurity.build();
    }



}
