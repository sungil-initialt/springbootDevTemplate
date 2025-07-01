package com.sptek._frameworkWebCore.util;

import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import com.sptek._frameworkWebCore.springSecurity.spt.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;

import java.util.*;

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

    // ANONYMOUS_USER 가 아닌 진짜 로그인 상태 인지 확인
    // SecurityUtil.getUserAuthentication().isAuthenticated() 을 사용 하지 않는 이유는 spring-security 가 비 로그인 상태도 anonymousUser 상태의 로그인 으로 간주 하는게 default 이기 때문
    // 디볼트 동작 으로 인한 장점이 있기 때문에.. 그대로 유지 시킴
    public static boolean isRealLogin() {
        return !CommonConstants.ANONYMOUS_USER.equals(SecurityUtil.getMyAuthentication().getPrincipal().toString());
    }

    // spring security 필터에 의해 처리된 접속자 정보(전체 정보)
    public static Authentication getMyAuthentication() {
        //log.debug("getMyAuthentication : {}", SecurityContextHolder.getContext().getAuthentication());
        return SecurityContextHolder.getContext().getAuthentication();
    }

    // spring security 필터에 의해 처리된 접속자 정보(정리된 정보)
    public static CustomUserDetails getMyCustomUserDetails() {
        //log.debug("getMyCustomUserDetails : {}", getMyAuthentication().getPrincipal());
        return (CustomUserDetails) getMyAuthentication().getPrincipal();
    }
    
    
//-->이걸로 롤만, 오스만 유니크하게 럴러주는거 만들어서 hasAccessFromPath 완성해야 함
    public static List<String> getUniqueGrantedAuthorities(String authType) {
        Set<String> uniqueGrantedAuthorities = new HashSet<>();
        ((CustomUserDetails) getMyAuthentication().getPrincipal()).getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(grantedAuthority -> grantedAuthority != null && grantedAuthority.startsWith(authType))
                .forEach(grantedAuthority -> uniqueGrantedAuthorities.add(grantedAuthority.substring(authType.length())));
        
        log.debug("getMyRoleList : {}", uniqueGrantedAuthorities);
        return new ArrayList<>(uniqueGrantedAuthorities);
    }
    
//    public static boolean hasAccessFromPath(String authPath, List<T> roleDtos) {
//        if (authPath == null || roleDtos == null || roleDtos.isEmpty()) return false;
//
//        Path path = Paths.get(authPath);
//        if (path.getNameCount() == 0) return false;
//
//        // 첫 번째 디렉토리 추출 (role-admin-adminTop-system)
//        String firstSegment = path.getName(0).toString();
//        if (!firstSegment.toLowerCase().startsWith("role-")) return false;
//
//        // 추출된 롤 키셋: [admin, adminTop, system]
//        Set<String> pathRoles = Arrays.stream(firstSegment.substring("role-".length()).split("-"))
//                .map(String::toLowerCase)
//                .collect(Collectors.toSet());
//
//        // 사용자의 ROLE_ 접두어 제거한 실제 롤 이름과 비교
//        for (T dto : roleDtos) {
//            String roleName = T instanceof RoleDto ((RoledDto)dto).getRoleName();
//            if (roleName != null && roleName.toUpperCase().startsWith("ROLE_")) {
//                String roleKey = roleName.substring("ROLE_".length()).toLowerCase();
//                if (pathRoles.contains(roleKey)) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
}
