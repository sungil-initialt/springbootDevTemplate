package com.sptek.webfw.config.springSecurity;

import com.sptek.webfw.config.springSecurity.extras.UserEntity;
import com.sptek.webfw.config.springSecurity.extras.UserRepository;
import com.sptek.webfw.util.ModelMapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
@Service("userDetailsService")
class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String userEmail) {
        Optional<UserEntity> userEntityOptional = userRepository.findByEmail(userEmail);

        // Optional의 orElseThrow를 사용해 예외 처리
        UserEntity userEntity = userEntityOptional.orElseThrow(
                () -> new UsernameNotFoundException(String.format("email '%s' not found", userEmail))
        );

        // CustomUserDetails 생성 및 반환
        return CustomUserDetails.builder()
                .id(userEntity.getId())
                .email(userEntity.getEmail())
                .password(userEntity.getPassword())
                .userRoleEntitySet(userEntity.getUserRoleEntitySet())
                .build();

        /*
        return ModelMapperUtil.map(userRepository.findByEmail(userEmail)
                        .orElseThrow(() -> new UsernameNotFoundException(String.format("email '%s' not found", userEmail)))
                , CustomUserDetails.class);
         */
    }
}