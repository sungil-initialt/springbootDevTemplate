package com.sptek._frameworkWebCore.util;

import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import com.sptek._frameworkWebCore.springSecurity.AuthorityEnum;
import com.sptek._frameworkWebCore.springSecurity.spt.CustomUserDetails;
import com.sptek._projectCommon.commonObject.code.SecureFilePathTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class SecurityUtil {

    /*주어진 html을 html 엔티티 코드로 변경해 준다.
    For example:
            "bread" &amp; "butter"
    becomes:
            &amp;quot;bread&amp;quot; &amp;amp; &amp;quot
    */
    public static String charEscape(String orgStr) {
        return orgStr == null ? orgStr : StringEscapeUtils.escapeHtml4(orgStr);
    }

    public static List<String> getNotEssentialRequestPatterns(){
        //return Arrays.asList("foo", "bar");
        return Arrays.asList(
                "/swagger-ui.html"
                , "/api-docs/**"
                , "/v2/api-docs/**"
                , "/v3/api-docs/**"
                , "/configuration/ui/**"
                , "/configuration/security/**"
                , "/swagger-resources/**"
                , "/swagger-ui/**"
                , "/swagger/**"
                , "/webjars/**"
                , "/error/**"
                , "/err/**"
                , "/static/**"
                , "/systemSupportApi/notEssential/**"
                , "/github-markdown-css/**"
                , "/h2-console/**"
                , "/static/favicon.ico"
        );
    }
    public static String[] getNotEssentialRequestPatternsArray() {
        List<String> patterns = getNotEssentialRequestPatterns();
        String[] patternsArray = patterns.toArray(new String[0]);
        return patternsArray;
    }

    public static boolean isNotEssentialRequest(){
        List<String> requestPatterns = getNotEssentialRequestPatterns();
        String requestPath = SpringUtil.getRequest().getServletPath();
        AntPathMatcher pathMatcher = new AntPathMatcher();

        for (String requestPattern : requestPatterns) {
            if(pathMatcher.match(requestPattern, requestPath))
                return true;
        }
        return false;
    }

    public static List<String> getStaticResourceRequestPatterns(){
        //return Arrays.asList("foo", "bar");
        return Arrays.asList(
                "/**/*.html", "/**/*.htm", "/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg", "/**/*.gif"
                , "/**/*.svg", "/**/*.webp", "/**/*.ico", "/**/*.mp4", "/**/*.webm", "/**/*.ogg", "/**/*.mp3", "/**/*.wav"
                , "/**/*.woff", "/**/*.woff2", "/**/*.ttf", "/**/*.otf", "/**/*.eot", "/**/*.pdf", "/**/*.xml", "/**/*.json"
                , "/**/*.csv", "/**/*.txt"
        );
    }

    public static String[] getStaticResourceRequestPatternArray() {
        List<String> patterns = getStaticResourceRequestPatterns();
        String[] patternsArray = patterns.toArray(new String[0]);
        return patternsArray;
    }

    public static boolean isStaticResourceRequest(){
        List<String> requestPatterns = getStaticResourceRequestPatterns();
        String requestPath = SpringUtil.getRequest().getServletPath();
        AntPathMatcher pathMatcher = new AntPathMatcher();

        for (String requestPattern : requestPatterns) {
            if(pathMatcher.match(requestPattern, requestPath))
                return true;
        }
        return false;
    }

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
        if(SecurityUtil.getMyAuthentication() == null)
            return false;
        return !CommonConstants.ANONYMOUS_USER.equals(SecurityUtil.getMyAuthentication().getPrincipal().toString());
    }

    // spring security 필터에 의해 처리된 접속자 정보(정리된 정보)
    public static CustomUserDetails getMyCustomUserDetails() throws Exception {
        //log.debug("getMyCustomUserDetails : {}", getMyAuthentication().getPrincipal());

        if (!(getMyAuthentication().getPrincipal() instanceof CustomUserDetails))
            throw new Exception("Not Login yet.");
        return (CustomUserDetails) getMyAuthentication().getPrincipal();
    }

    private static Set<String> getMyAuthorities(String authFilterStr) {
        Set<String> uniqueGrantedAuthorities = new HashSet<>();
        getMyAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(grantedAuthority -> grantedAuthority != null && grantedAuthority.startsWith(authFilterStr))
                // ROLE_, AUTH_ prefix 를 제외 할지 여부
                // .forEach(grantedAuthority -> uniqueGrantedAuthorities.add(grantedAuthority.substring(authFilterStr.length())));
                .forEach(uniqueGrantedAuthorities::add);

        log.debug("getMy{} : {}", authFilterStr, uniqueGrantedAuthorities);
        return uniqueGrantedAuthorities;
    }

    public static Set<String> getMyRole() {
        return getMyAuthorities("ROLE_");
    }

    public static Set<String> getMyAuth() {
        return getMyAuthorities("AUTH_");
    }

    public static Path getSecuredFilePathForAnyone() {
        return Path.of(SecureFilePathTypeEnum.ANYONE.getPathName());
    }

    public static Path getSecuredFilePathForLogin() {
        // 만드는 시점 에도 로그인 상태가 체크가 필요 할까?
        return Path.of(SecureFilePathTypeEnum.LOGIN.getPathName());
    }

    public static Path getSecuredFilePathForUser() throws Exception {
        if (!SecurityUtil.isRealLogin()) throw new Exception("로그인 상태가 아닙니다.");
        return Path.of(SecureFilePathTypeEnum.USER.getPathName(), String.valueOf(SecurityUtil.getMyCustomUserDetails().getUserDto().getId()));
    }

    public static Path getSecuredFilePathForRole(Set<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) throw new IllegalArgumentException("roleNames is required");
        return Path.of(SecureFilePathTypeEnum.ROLE.getPathName(), String.join("-", roleNames));
    }

    public static Path getSecuredFilePathForAuth(Set<AuthorityEnum> Authorities) {
        if (Authorities == null || Authorities.isEmpty()) throw new IllegalArgumentException("Authorities is required");
        return Path.of(SecureFilePathTypeEnum.AUTH.getPathName()
                , Authorities.stream().map(AuthorityEnum::name).collect(Collectors.joining("-")));
    }

    public static Path getStorageRootPath(Path securedFilePath) {
        if (securedFilePath == null) throw new IllegalArgumentException("securedFilePath is required");
        try {
            return Path.of(String.valueOf(SpringUtil.getApplicationProperty(String.format("storage.%s.localRootPath", securedFilePath.getName(0).toString()))));
        } catch (Exception e) {
            throw new IllegalArgumentException("Unsupported SecurePathEnum value: " + securedFilePath.getName(0).toString());
        }
    }

    //현재 사용자 가 해당 securedFilePath 에 Access 권한이 있는지 확인 함
    public static boolean hasPermissionForSecuredFilePath(Path securedFilePath) throws Exception {
        if (securedFilePath == null) throw new IllegalArgumentException("securedFilePath is required");

        try {
            String SecurePathType = securedFilePath.getName(0).toString();

            if (SecurePathType.equals(SecureFilePathTypeEnum.ANYONE.getPathName())) {
                return true;

            } else if (SecurePathType.equals(SecureFilePathTypeEnum.LOGIN.getPathName())) {
                return SecurityUtil.isRealLogin();

            } else if (SecurePathType.equals(SecureFilePathTypeEnum.USER.getPathName())) {
                return securedFilePath.getName(1).toString().equals(String.valueOf(SecurityUtil.getMyCustomUserDetails().getUserDto().getId()));

            } else if (SecurePathType.equals(SecureFilePathTypeEnum.ROLE.getPathName())) {
                Set<String> rolesInSecuredFilePath = Arrays.stream(securedFilePath.getName(1).toString().split("-")).collect(Collectors.toSet());
                return !Collections.disjoint(rolesInSecuredFilePath, SecurityUtil.getMyRole());

            } else if (SecurePathType.equals(SecureFilePathTypeEnum.AUTH.getPathName())) {
                Set<String> authsInSecuredFilePath = Arrays.stream(securedFilePath.getName(1).toString().split("-")).collect(Collectors.toSet());
                return !Collections.disjoint(authsInSecuredFilePath, SecurityUtil.getMyAuth());

            } else {
                throw new Exception("Unsupported securedFilePath: " +  securedFilePath);
            }
        } catch (Exception ex) {
            throw new Exception("Unsupported securedFilePath: " +  securedFilePath);
        }
    }
}
