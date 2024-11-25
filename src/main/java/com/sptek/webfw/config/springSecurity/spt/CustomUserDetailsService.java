package com.sptek.webfw.config.springSecurity.spt;

import com.sptek.webfw.config.springSecurity.extras.dto.UserDto;
import com.sptek.webfw.config.springSecurity.extras.entity.User;
import com.sptek.webfw.config.springSecurity.extras.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//todo : custom하다고다 볼수 있나? custom이란 관점에서 UserDetailsService가 여러개 존재 할수 있지만 현재는 하나 임으로 "userDetailsService" 라는 기본형 네임을 여기에 달아줌
@Slf4j
@Service("userDetailsService")
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public CustomUserDetails loadUserByUsername(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("email '%s' not found", userEmail)));
        log.debug("user info: {}", user);

        UserDto userDto = modelMapper.map(user, UserDto.class);
        return CustomUserDetails.builder().userDto(userDto).build();
    }
}