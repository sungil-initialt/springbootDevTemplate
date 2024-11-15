package com.sptek.webfw.config.springSecurity.spt;

import com.sptek.webfw.config.springSecurity.extras.dto.RoleDto;
import com.sptek.webfw.config.springSecurity.extras.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomUserDetails  implements UserDetails {
    private UserDto userDto;

    @Override
    public String getUsername() { //보통? 계정 정보를 의미함 그래서.. 사용자 이름이 아니라 계정 정보로 사용되는 email을 넘기도록 처리
        log.debug("username : {} ", userDto.getEmail());
        return userDto.getEmail();
    }

    @Override
    public String getPassword() {
        log.debug("password : {} ", userDto.getPassword());
        return userDto.getPassword();
    }

    //UserDetails 인터페이스에는 없어서 사람의 실제 이름을 추가함
    public String getUserRealName() {
        log.debug("userRealName : {} ", userDto.getName());
        return userDto.getName();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //spring security 에서의 권한은 Role과 Authority 를 따로 구분하지 않는듯,
        // 모두 Authentication 의  getAuthorities() 를 통해 제공되며 Role 경우 이름에 프리픽스로 ROLE_xx 를 관습적으로 남김
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        for (RoleDto role : userDto.getRoles()) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getRoleName())); //Role을 auth에 추가
            Optional.ofNullable(role.getAuthorityEnums()) //auth도 auth에 추가
                    .ifPresent(authorityEnums -> {
                        authorityEnums.forEach(authorityEnum -> {
                            grantedAuthorities.add(new SimpleGrantedAuthority(authorityEnum.name()));
                        });
                    });
        }
        return grantedAuthorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정의 만료 여부
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정의 잠김 여부
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // 비밀번호 만료 여부
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정의 활성화 여부
    }
}
