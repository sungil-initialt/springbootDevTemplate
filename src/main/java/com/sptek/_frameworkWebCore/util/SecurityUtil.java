package com.sptek._frameworkWebCore.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;

import java.util.Arrays;
import java.util.List;

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
                , "/health/**"
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
                "/**/*.html**", "/**/*.htm**", "/**/*.css**", "/**/*.js**", "/**/*.png**", "/**/*.jpg**", "/**/*.jpeg**", "/**/*.gif**"
                , "/**/*.svg**", "/**/*.webp**", "/**/*.ico**", "/**/*.mp4**", "/**/*.webm**", "/**/*.ogg**", "/**/*.mp3**", "/**/*.wav**"
                , "/**/*.woff**", "/**/*.woff2**", "/**/*.ttf**", "/**/*.otf**", "/**/*.eot**", "/**/*.pdf**", "/**/*.xml**", "/**/*.json**"
                , "/**/*.csv**", "/**/*.txt**"
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

    // spring security 필터에 의해 처리된 접속자 정보
    public static Authentication getUserAuthentication() {
        //log.debug("UserAuthentication : {}", SecurityContextHolder.getContext().getAuthentication());
        return SecurityContextHolder.getContext().getAuthentication();
    }

}
