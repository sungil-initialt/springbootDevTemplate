package com.sptek.webfw.config.springSecurity;

import com.sptek.webfw.config.springSecurity.service.UserRepository;
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
        return ModelMapperUtil.of(userRepository.findByEmail(userEmail)
                        .orElseThrow(() -> new BadCredentialsException("이메일 주소를 확인해주세요."))
                , CustomUserDetails.class);
    }
}