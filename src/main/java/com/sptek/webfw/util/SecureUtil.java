package com.sptek.webfw.util;

import org.apache.commons.lang3.StringEscapeUtils;

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

    public static List<String> getStaticFileExtensionsPatternList(){
        return Arrays.asList(
                "/**/*.html**", "/**/*.htm**", "/**/*.css**", "/**/*.js**", "/**/*.png**", "/**/*.jpg**", "/**/*.jpeg**", "/**/*.gif**",
                "/**/*.svg**", "/**/*.webp**", "/**/*.ico**", "/**/*.mp4**", "/**/*.webm**", "/**/*.ogg**", "/**/*.mp3**", "/**/*.wav**",
                "/**/*.woff**", "/**/*.woff2**", "/**/*.ttf**", "/**/*.otf**", "/**/*.eot**", "/**/*.pdf**", "/**/*.xml**", "/**/*.json**",
                "/**/*.csv**", "/**/*.txt**"
        );
    }



}
