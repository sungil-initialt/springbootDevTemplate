package com.sptek.webfw.config.springSecurity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/*
AuthenticationProvider 와 UserDetailsService를 만드는 주체가 별개일수 있는 관점에서 봐야 함 (왜 이런 여러 단계를 두었을까에 대한..)
그래서 AuthenticationProvider 의 실질적인 처리를 위한 UserDetailsService를 주입받는 것이고
또 그래서 서로 다른 주체간에 interface 를 마추기 위해서  UserDetailsService 의 loadUserByUsername 처리 리턴을 UserDetails 로 규정해 놓은 것임
ex:
정책은 통합으로 한곳(AuthenticationProvider)에서 만들어서 내려주는데 서비스별로 User테이블의 컬럼이름도 다 다르고 할수 있음 (여기서는 id 격의 값을 email을 사용)
그래서 UserDetailsService 는 서비스 별로 각자 구현해서 최종 결과만 UserDetails 규격으로 리턴해 주면 통일된 동작이 가능함.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        //spring security 의 AuthenticaionFilter 에서 UsernamePasswordAuthenticationToken를 생성해줌
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;

        //현재 서비스에서는 email 값을 Name으로 활용하고 있음
        String email = token.getName();
        String password = (String) token.getCredentials();
        log.debug("user info : {} , {}", email, password);

        // DB 에서 사용자 정보가 실제로 유효한지 확인 후 인증된 Authentication 리턴
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(email);
        if (bCryptPasswordEncoder.matches(password, customUserDetails.getPassword()) == false) {
            throw new BadCredentialsException(customUserDetails.getUsername() + " : Password does not match.");
        }
        //todo : principal 로 customUserDetails 전체를 주는게 맞을까? name만 주는게 맞을까?
        //return new UsernamePasswordAuthenticationToken(customUserDetails.getUsername(), password, customUserDetails.getAuthorities());
        return new UsernamePasswordAuthenticationToken(customUserDetails, password, customUserDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}