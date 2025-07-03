package com.sptek._frameworkWebCore.util;

import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import com.sptek._frameworkWebCore.springSecurity.AuthorityIfEnum;
import com.sptek._frameworkWebCore.springSecurity.spt.CustomUserDetails;
import com.sptek._projectCommon.commonObject.code.SecurePathTypeEnum;
import com.sptek._projectCommon.commonObject.dto.FileStorageDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    private static Set<String> getMyAuthorities(String filterStr) {
        Set<String> uniqueGrantedAuthorities = new HashSet<>();
        getMyAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(grantedAuthority -> grantedAuthority != null && grantedAuthority.startsWith(filterStr))
                .forEach(grantedAuthority -> uniqueGrantedAuthorities.add(grantedAuthority.substring(filterStr.length())));

        log.debug("getMy{} : {}", filterStr, uniqueGrantedAuthorities);
        return uniqueGrantedAuthorities;
    }

    public static Set<String> getMyRole() {
        return getMyAuthorities("ROLE_");
    }

    public static Set<String> getMyAuth() {
        return getMyAuthorities("AUTH_");
    }

    public static FileStorageDto makeFileStoragePath(SecurePathTypeEnum securePathTypeEnum, @Nullable Long userId, @Nullable List<String> roleNames, @Nullable List<AuthorityIfEnum> authorityIfEnums) throws Exception {
        // todo: 하나의 폴더에 file 또는 dir 이 무수히 (백만, 천만) 늘어 날때의 해결이 필요함

        Path rootPath = Path.of(String.valueOf(SpringUtil.getApplicationProperty(String.format("storage.%s.localRootPath", securePathTypeEnum.getPathName()))));
        switch (securePathTypeEnum) {
            case ANYONE, LOGIN:
                return new FileStorageDto(rootPath, Path.of(securePathTypeEnum.getPathName()));

            case USER:
                if (userId == null) throw new Exception("userId is required");
                return new FileStorageDto(rootPath, Path.of(securePathTypeEnum.getPathName(), userId.toString()));

            case ROLE:
                if (roleNames == null) throw new Exception("roleNames is required");
                String roleNamesStr = String.join("-", roleNames);
                return new FileStorageDto(rootPath, Path.of(securePathTypeEnum.getPathName(), roleNamesStr));

            case AUTH:
                if (authorityIfEnums == null) throw new Exception("authorityIfEnums is required");
                String authorityIfEnumsStr = authorityIfEnums.stream().map(AuthorityIfEnum::name).collect(Collectors.joining("-"));
                return new FileStorageDto(rootPath, Path.of(securePathTypeEnum.getPathName(), authorityIfEnumsStr));

            default:
                throw new IllegalArgumentException("Unsupported SecurePathEnum value: " + securePathTypeEnum);
        }
    }

}
