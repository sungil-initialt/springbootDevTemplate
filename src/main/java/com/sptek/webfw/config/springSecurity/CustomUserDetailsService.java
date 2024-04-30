package com.sptek.webfw.config.springSecurity;

import com.sptek.webfw.config.springSecurity.support.UserRepository;
import com.sptek.webfw.util.ModelMapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String userEmail) {
        return ModelMapperUtil.map(userRepository.findByEmail(userEmail)
                        .orElseThrow(() -> new BadCredentialsException("이메일 주소를 확인해주세요."))
                , CustomUserDetails.class);
    }

    //-->여기부터 고민해 보쟈!!
}