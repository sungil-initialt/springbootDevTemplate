package com.sptek.webfw.config.springSecurity.spt;

import com.sptek.webfw.config.springSecurity.extras.entity.User;
import com.sptek.webfw.config.springSecurity.extras.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service("userDetailsService")
class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format("email '%s' not found", userEmail)
                ));

        // CustomUserDetails 생성 및 반환
        return CustomUserDetails.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .build();

        /*
        //ModelMapperUtil에 의존하는게 별로인듯
        return ModelMapperUtil.map(userRepository.findByEmail(userEmail)
                        .orElseThrow(() -> new UsernameNotFoundException(String.format("email '%s' not found", userEmail)))
                , CustomUserDetails.class);
         */
    }
}