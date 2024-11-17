package com.sptek.webfw.config.springSecurity;

import com.sptek.webfw.config.springSecurity.spt.CustomAuthenticationProvider;
import com.sptek.webfw.config.springSecurity.spt.CustomJwtFilter;
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

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final GeneralTokenProvider generalTokenProvider;

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
    //로그인 전체 스템을 관리할 AuthenticationManager(=ProviderManager)에 AuthenticationProvider을 추가하여 반환. (필요에 따라 만들어진 AuthenticationProvider)
    public AuthenticationManager authManager(HttpSecurity httpSecurity) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(new CustomAuthenticationProvider());
        return authenticationManagerBuilder.build();
    }

    @Bean
    //스프링 6.x 버전부터 변경된 방식으로, spring security는 자체적으로 준비된 필터들과 동작 순서가 있으며 아래는 그 필터들의 동작유무 및 설정 옵션을 지정하는 역할을 한다.
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // JWT를 사용하는 경우 CSRF방지 기능을 사용 할 필요가 없음.(CSRF 공격이 session 쿠키 방식의 문제 점에 기인한 것으로 JWT만 사용하여 세션을 사용하지 않는다면 disable 처리)
                .csrf(csrf ->
                    csrf.disable())

                //특정 경로에 특정 Role을 지정함
                .authorizeHttpRequests(authorize ->
                    authorize
                            .requestMatchers("/signup", "/signin", "/login", "/api/auth").permitAll()  //인증 처리를 위한 오픈 경로(signin은 커스텀하게 만든 페이지, login은 디폴트로 있던..)
                            .requestMatchers("/admin/**", "/api/admin/**").hasRole("ADMIN")
                            .requestMatchers("/my/**", "/mypage/**", "/api/**").hasAnyRole("ADMIN", "USER")
                            //.anyRequest().permitAll() //그외
                            .anyRequest().authenticated() //그외
                )

                //인증과 관련된 EX 처리 주체 설정
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                //인증 오류를 케치하여 처리(todo : 비설정시 login 페이지로 이동됨, 반대로 설정되면 로그인 페이지로 자동 연결되지 않음, 필터의 순서상의 이유가 있는 듯, 자세한건 확인 필요)
                                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                                .accessDeniedHandler(jwtAccessDeniedHandler) //인가 오류 진입점
                )

                //security와 관련해서 custom하게 만든 필터가 있다면 적정 위치에 추가할 수 있다.
                //(UsernamePasswordAuthenticationFilter 은 스프링 자체 필터로, post 방식, /login 경로 요청시 동작하며 해당 req post로 전달된 정보를 이용해 스프링의 authenticationManager 통한 인증 절차를 요청함)
                .addFilterBefore(new CustomJwtFilter(generalTokenProvider), UsernamePasswordAuthenticationFilter.class)

                //.httpBasic(Customizer.withDefaults()) //얼럿창형
                //.formLogin(withDefaults()) //form형 디폴트 로그인 (--:8443/login 으로 고정되어 있는듯 8443 포트에서만 정상 동작됨)
                .formLogin(form -> form
                        .loginPage("/signin") //custom 로그인 폼
                );

                //로그인 페이지에서 서브밋되는 곳은 컨트럴러를 만들어서 처리하는게 아니라 오스메니터저(유저디테일서비스, 유저디테일) 를 통해 처리되고 그 결과도 컨텍스트홀드에 저장되었느니 안되었느냐로 처리되는듯 함
                //그래서 jwt 방식의 처리는(기본필터는 없는듯) 오스핸티 기본 필터의 앞에 위치하게 해서 만들고 id/ps 받아서 처리되는것을 그앞에 처리해 버리기 위해,
                //그런데 jwt 방식의 경우 로그인 처리 자체도 api 를 통해 이뤄저야 함으로 로그인처리 api 의 경우 퍼밋올로 해주고
                //해당 컨트롤러 내부에서 오쓰매니저를 직접 만들어서 포로그인때와 뒷쪽으론 동일하게 처리되도록 한다.
                //단 컨텍스호드에 인증정보를 너줄찌.. 해더에만 jwt로 내릴지는 자체적으로 판단해야 할듯하다.

                //UsernamePasswordAuthenticationFilter 의 코드 내용을 보면 로그인 페이지로 설정된 경로로 post 호출이 발생하면 id,pw 파람을 추출해서 오쓰매니저에 인증처리를 하게 요청하는 내용임

        return httpSecurity.build();
    }
}
