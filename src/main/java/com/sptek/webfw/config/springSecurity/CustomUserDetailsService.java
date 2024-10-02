package com.sptek.webfw.config.springSecurity;

import com.sptek.webfw.config.springSecurity.extras.UserRepository;
import com.sptek.webfw.util.ModelMapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String userEmail) {
        return ModelMapperUtil.map(userRepository.findByEmail(userEmail)
                        .orElseThrow(() -> new UsernameNotFoundException(String.format("email '%s' not found", userEmail)))
                , CustomUserDetails.class);
    }

    //-->여기부터 고민해 보쟈!!
}