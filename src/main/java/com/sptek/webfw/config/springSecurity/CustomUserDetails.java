package com.sptek.webfw.config.springSecurity;

import com.sptek.webfw.config.springSecurity.extras.TermsEntity;
import com.sptek.webfw.config.springSecurity.extras.RoleEntity;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@NoArgsConstructor
public class CustomUserDetails  implements UserDetails {
    private long id;
    private String email; //보여지는 계정 정보로 사용
    private String password;
    private String name; //사용자 이름
    private Set<RoleEntity> roleEntitySet;
    private Set<TermsEntity> termsEntitySet; //추가적인 커스텀 요소

    @Builder //특정 필드만 적용하기 위해
    private CustomUserDetails(long id, String email, String password, Set<RoleEntity> roleEntitySet, Set<TermsEntity> termsEntitySet) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.roleEntitySet = roleEntitySet;
        this.termsEntitySet = termsEntitySet;
    }

    @Override
    public String getUsername() { //보통? 계정 정보를 의미함 그래서.. 사용자 이름이 아니라 계정 정보로 사용되는 email을 넘기도록 처리
        log.debug("getUsername : {} ", email);
        return this.email;
    }

    @Override
    public String getPassword() {
        log.debug("getPassword : {} ", email);
        return this.password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 계정의 권한 목록
        Set<GrantedAuthority> grantedAuthoritySet = new HashSet<>();

        for (RoleEntity roleEntity : roleEntitySet) {
            grantedAuthoritySet.add(new SimpleGrantedAuthority(roleEntity.getRoleEnum().getValue())); // 역할 값을 GrantedAuthority로 변환하여 추가
        }
        return grantedAuthoritySet;
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
