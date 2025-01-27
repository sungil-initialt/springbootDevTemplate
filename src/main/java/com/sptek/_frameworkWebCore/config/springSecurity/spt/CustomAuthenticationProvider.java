package com.sptek._frameworkWebCore.config.springSecurity.spt;

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
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = (UsernamePasswordAuthenticationToken) authentication;

        // email 값을 Name(일반적id개념) 으로 사용하는 케이스
        String email = usernamePasswordAuthenticationToken.getName();
        String password = (String) usernamePasswordAuthenticationToken.getCredentials();
        log.debug("requested id, ps : {}, {}", email, bCryptPasswordEncoder.encode(password));

        // 해당 계정이 존재하는지 확인(password 확인은 하지 않음)
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(email);
        log.debug("user info from userDetailsService by userName : {}, {}, {}", customUserDetails.getUsername(), customUserDetails.getPassword(), customUserDetails.getAuthorities());

        // password 가 일치 하지 않으면 BadCredentialsException 처리
        if (!bCryptPasswordEncoder.matches(password, customUserDetails.getPassword())) {
            log.debug("Password does not match.");
            throw new BadCredentialsException(customUserDetails.getUsername() + " : Password does not match.");
        } else {
            log.debug("Password matched.");
        }

        // todo : principal 로 customUserDetails 전체를 주는게 맞을까? name만 주는게 맞을까?(principal 은 Object 타입으로 서비스상 필요한 적절한 객체로 전달하면 됨으로.. 일단 전체 정보인 customUserDetails로 넘김)
        // Provider는 생성한 Authentication을 리턴할뿐 SecurityContextHolder에 저장하는 역할은 하지 않아야 함 (Provider가 여럿 설정된 상황일수 있으니 AuthenticationManager 에게 맞겨야함)
        // 비밀번호는 민감정보로 매칭 확인 후 유출을 막기 위에 처리함 (필요시 넘길수도 있겠지...)
        customUserDetails.getUserDto().setPassword("[PROTECTED]");
        //password = "[PROTECTED]"; //Authentication에 들어가는 password(credentials)은 자동 PROTECTED 처리됨
        return new UsernamePasswordAuthenticationToken(customUserDetails, password, customUserDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        // 내가 어떤 종류의 Token을 받았을때 처리할수 있는지를 알려주는 것이다.
        // authenticationManager는 자기에 등록되어 있는 authenticationProvider 목록에서 해당 token을 지원하는 provider 들에게 인증 처리를 요청하게 된다.
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}