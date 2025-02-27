package com.sptek._frameworkWebCore.springSecurity.spt;

import com.sptek._frameworkWebCore.springSecurity.extras.dto.RoleDto;
import com.sptek._frameworkWebCore.springSecurity.extras.dto.UserDto;
import lombok.*;
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
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomUserDetails  implements UserDetails {
    private UserDto userDto;

    @Override
    public String getUsername() { //보통? 계정 정보를 의미함 그래서.. 사용자 이름이 아니라 계정 정보로 사용되는 email을 넘기도록 처리
        return userDto.getEmail();
    }

    @Override
    public String getPassword() {
        return userDto.getPassword();
    }

    //UserDetails 인터페이스에는 없어서 사람의 실제 이름을 추가함
    public String getUserRealName() {
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

    //아래 계정 상태와 관련된 메소드를 각 사이트에 맞게 처리하여 리턴하도록 함
    //DaoAuthenticationProvider 와 같은 제공되는? Provider 들은 내부의 additionalAuthenticationChecks 메소드를 통해 검증 절차를 진행함
    //custom으로 Provider 를 만드는 경우 Provider 내부에서 호출해서 처리하로독 한다. (DaoAuthenticationProvider 내부를 참고)
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
