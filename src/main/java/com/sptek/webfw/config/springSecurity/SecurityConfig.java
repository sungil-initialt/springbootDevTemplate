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
    //private final TokenProvider tokenProvider;
    private final CustomAuthenticationProvider customAuthenticationProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final GeneralTokenProvider generalTokenProvider;

    //비밀번호 암호화에 사용할 Bean
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
                // JWT를 사용하는 경우 CSRF방지 기능을 사용 할 필요가 없음.(CSRF 공격이 session 쿠키 방식의 문제 점에 기인한 것으로 JWT는 세션이 기반이 아니기 때문에)
                .csrf(csrf ->
                    csrf.disable())

                //추가할 필터들 정의
                .addFilterBefore(new CustomJwtFilter(generalTokenProvider), UsernamePasswordAuthenticationFilter.class)

                //Request Matchers, 설정된 경로와 Role이 일치하지 않는 경우 Exception 발생시김, 해당 ex는 authenticationEntryPoint 설정으로 전달
                .authorizeHttpRequests(authorize ->
                    authorize
                            .requestMatchers("/signup", "/signin", "/login", "/api/auth").permitAll()  //인증 처리를 위한 오픈 경로(signin은 직접 만든 로그인 페이지의 호출용, login은 디폴트를 사용했을대의 경로)
                            .requestMatchers("/admin/**", "/api/admin/**").hasRole("ADMIN")
                            .requestMatchers("/my/**", "/mypage/**", "/api/**").hasAnyRole("ADMIN", "USER")
                            //.anyRequest().permitAll() //그외
                            .anyRequest().authenticated() //그외
                )

                //인증과 관련된 EX 처리 주체 설정
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                //.authenticationEntryPoint(jwtAuthenticationEntryPoint) //인증 오류 진입점, 비설정시 login 페이지로 이동됨(반대로 설정되면 로그인 페이지로 자동 연결되지 않음)
                                .accessDeniedHandler(jwtAccessDeniedHandler) //인가 오류 진입점
                )

                //로그인 페이지로 httpBasic 또는 formLogin을 사용하려면 exceptionHandling을 설정하지 말아야 함(설정시 핸들러에서 먼저 처리됨)
                //.httpBasic(Customizer.withDefaults()) //얼럿창형 디폴트 로그인
                //.formLogin(withDefaults()) //form형 디폴트 로그인 (--:8443/login 으로 고정되어 있는듯 8443 포트로 돌릴때만 정상 동작됨)
                .formLogin(form -> form
                        .loginPage("/signin") //해당 경로는 permit 이 open되어 있어야 함
                );




                        //생각내용 정리
        /*        UsernamePasswordAuthenticationFilter 는  security 필터에 기본으로 포함하는 필터 이며
                인증되야 하는 페이지로 설정된 페이지로 접근했을때 적용되는 필터이며 인증값이 홀더에 없으면 인증처리를 하기위한 로그인폼 페이지를 내려주는 역할을 한다.
                또 그 폼을 통해 로그인이 처리되도록 로그인 처리url 에도 관여가 될것으로 생각되고 */



        //스프링 시큐리티에 미리 준비된 필터의 경우 순서는 이미 정의되어 있는듯(아래의 코드는 설정의 의미이지 순서의 의미는 아닌듯, 단 추가로 만드는 필터의 경우 어디사이로 들어갈지 순서가 중요)
        //로그인 페이지에서 서브밋되는 곳은 컨트럴러를 만들어서 처리하는게 아니라 오스메니터저(유저디테일서비스, 유저디테일) 를 통해 처리되고 그 결과도 컨텍스트홀드에 저장되었느니 안되었느냐로 처리되는듯 함
        //그래서 jwt 방식의 처리는(기본필터는 없는듯) 오스핸티 기본 필터의 앞에 위치하게 해서 만들고 id/ps 받아서 처리되는것을 그앞에 처리해 버리기 위해,
        //그런데 jwt 방식의 경우 로그인 처리 자체도 api 를 통해 이뤄저야 함으로 로그인처리 api 의 경우 퍼밋올로 해주고
        //해당 컨트롤러 내부에서 오쓰매니저를 직접 만들어서 포로그인때와 뒷쪽으론 동일하게 처리되도록 한다.
        //단 컨텍스호드에 인증정보를 너줄찌.. 해더에만 jwt로 내릴지는 자체적으로 판단해야 할듯하다.

        //UsernamePasswordAuthenticationFilter 의 코드 내용을 보면 로그인 페이지로 설정된 경로로 post 호출이 발생하면 id,pw 파람을 추출해서 오쓰매니저에 인증처리를 하게 요청하는 내용임


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
