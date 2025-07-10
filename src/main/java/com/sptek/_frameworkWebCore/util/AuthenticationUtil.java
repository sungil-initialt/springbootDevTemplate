package com.sptek._frameworkWebCore.util;

import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import com.sptek._frameworkWebCore.springSecurity.extras.dto.UserDto;
import com.sptek._frameworkWebCore.springSecurity.spt.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashSet;
import java.util.Set;

@Slf4j
public class AuthenticationUtil {

    // spring security 필터에 의해 처리된 접속자 정보(전체 정보)
    public static Authentication getMyAuthentication() {
        //log.debug("getMyAuthentication : {}", SecurityContextHolder.getContext().getAuthentication());
        return SecurityContextHolder.getContext().getAuthentication();
    }

    // ANONYMOUS_USER 가 아닌 진짜 로그인 상태 인지 확인
    // SecurityUtil.getUserAuthentication().isAuthenticated() 을 사용 하지 않는 이유는 spring-security 가 비 로그인 상태도 anonymousUser 상태의 로그인 으로 간주 하는게 default 이기 때문
    // 디볼트 동작 으로 인한 장점이 있기 때문에.. 그대로 유지 시킴
    public static boolean isRealLogin() {
        // spring security 가 다 로딩 되기 전의 호출이나 filter chain 이 적용 되지 않는 request 등을 위한 방어
        try {
            if(AuthenticationUtil.getMyAuthentication() == null)
                return false;
            return !CommonConstants.ANONYMOUS_USER.equals(AuthenticationUtil.getMyAuthentication().getPrincipal().toString());
        } catch (Exception e) {
            return false;
        }
    }

    // spring security 필터에 의해 처리된 접속자 정보(정리된 정보)
    public static UserDto getMyUserDto() {
        if (!isRealLogin()) return null;
        try {
            return ((CustomUserDetails) AuthenticationUtil.getMyAuthentication().getPrincipal()).getUserDto();
        } catch (ClassCastException e) {
            // todo: ClassCastException 하는 이유는 sessionId로(view) 인증 받는 케이스와 JWT로 인증 받는 케이스에 SecurityContextHolder 의 Authentication 정보 구조가 서로 다르기 때문임
            return null;
        }
    }

    public static Long getMyId() {
        if (!isRealLogin()) return null;
        try {
            return ((CustomUserDetails) AuthenticationUtil.getMyAuthentication().getPrincipal()).getUserDto().getId();
        } catch (ClassCastException e) {
            return null;
        }
    }

    public static String getMyName() {
        if (!isRealLogin()) return CommonConstants.ANONYMOUS_USER;
        try {
            return ((CustomUserDetails) AuthenticationUtil.getMyAuthentication().getPrincipal()).getUserDto().getEmail();
        } catch (ClassCastException e) {
            return ((UserDetails)AuthenticationUtil.getMyAuthentication().getPrincipal()).getUsername();
        }
    }

    public static String getMyEmail() {
        if (!isRealLogin()) return null;
        try {
            return ((CustomUserDetails) AuthenticationUtil.getMyAuthentication().getPrincipal()).getUserDto().getEmail();
        } catch (ClassCastException e) {
            return null;
        }
    }

    public static Set<String> getMyRoles() {
        return getMyAuthorities("ROLE_");
    }

    public static Set<String> getMyAuths() {
        return getMyAuthorities("AUTH_");
    }

    private static Set<String> getMyAuthorities(String authFilterStr) {
        Set<String> uniqueGrantedAuthorities = new HashSet<>();
        getMyAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(grantedAuthority -> grantedAuthority != null && grantedAuthority.startsWith(authFilterStr))
                // ROLE_, AUTH_ prefix 를 제외 할지 여부
                // .forEach(grantedAuthority -> uniqueGrantedAuthorities.add(grantedAuthority.substring(authFilterStr.length())));
                .forEach(uniqueGrantedAuthorities::add);

        //log.debug("getMy{} : {}", authFilterStr, uniqueGrantedAuthorities);
        return uniqueGrantedAuthorities;
    }
}
