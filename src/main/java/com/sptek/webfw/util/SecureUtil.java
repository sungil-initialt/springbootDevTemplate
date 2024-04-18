package com.sptek.webfw.util;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.util.AntPathMatcher;

import java.util.Arrays;
import java.util.List;

public class SecureUtil {

    /*주어진 html을 html 엔티티 코드로 변경해 준다.
    For example:
            "bread" &amp; "butter"
    becomes:
            &amp;quot;bread&amp;quot; &amp;amp; &amp;quot
    */
    public static String charEscape(String orgStr) {
        return orgStr == null ? orgStr : StringEscapeUtils.escapeHtml4(orgStr);
    }

    public static List<String> getNotEssentialRequestPatternList(){
        return Arrays.asList(
                "/swagger-ui.html",
                "/api-docs/**",
                "/v2/api-docs/**",
                "/configuration/ui/**",
                "/configuration/security/**",
                "/swagger-resources/**",
                "/swagger-ui/**",
                "/swagger/**",
                "/webjars/**",
                "/error/**",
                "/err/**",
                "/static/**",
                "/health/**",
                "/github-markdown-css/**",
                "/h2-console/**",
                "/favicon.ico"
        );
    }
    public static String[] getNotEssentialRequestPatternsArray() {
        List<String> patternList = getNotEssentialRequestPatternList();
        String[] patternsArray = patternList.toArray(new String[0]);
        return patternsArray;
    }

    public static boolean isNotEssentialRequest(){
        List<String> requestPatternList = getNotEssentialRequestPatternList();
        String requestPath = ReqResUtil.getRequest().getServletPath();
        AntPathMatcher pathMatcher = new AntPathMatcher();

        for (String requestPattern : requestPatternList) {
            if(pathMatcher.match(requestPattern, requestPath))
                return true;
        }
        return false;
    }

    public static List<String> getStaticResourceRequestPatternList(){
        return Arrays.asList(
                "/**/*.html**", "/**/*.htm**", "/**/*.css**", "/**/*.js**", "/**/*.png**", "/**/*.jpg**", "/**/*.jpeg**", "/**/*.gif**",
                "/**/*.svg**", "/**/*.webp**", "/**/*.ico**", "/**/*.mp4**", "/**/*.webm**", "/**/*.ogg**", "/**/*.mp3**", "/**/*.wav**",
                "/**/*.woff**", "/**/*.woff2**", "/**/*.ttf**", "/**/*.otf**", "/**/*.eot**", "/**/*.pdf**", "/**/*.xml**", "/**/*.json**",
                "/**/*.csv**", "/**/*.txt**"
        );
    }

    public static String[] getStaticResourceRequestPatternArray() {
        List<String> patternList = getStaticResourceRequestPatternList();
        String[] patternsArray = patternList.toArray(new String[0]);
        return patternsArray;
    }

    public static boolean isStaticResourceRequest(){
        List<String> requestPatternList = getStaticResourceRequestPatternList();
        String requestPath = ReqResUtil.getRequest().getServletPath();
        AntPathMatcher pathMatcher = new AntPathMatcher();

        for (String requestPattern : requestPatternList) {
            if(pathMatcher.match(requestPattern, requestPath))
                return true;
        }
        return false;
    }
}
