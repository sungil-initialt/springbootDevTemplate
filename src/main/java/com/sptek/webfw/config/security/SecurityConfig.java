package com.sptek.webfw.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

@Slf4j
@Configuration
public class SecurityConfig {

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (webSecurity) -> webSecurity.ignoring()
                .requestMatchers("/**");
    }
    /*
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // antMatchers 부분도 deprecated 되어 requestMatchers로 대체
        List<String> excludedPattern = SecureUtil.getNotEssentialRequestPatternList();
        excludedPattern.addAll(SecureUtil.getStaticResourceRequestPatternList());
        String[] array = excludedPattern.toArray(new String[excludedPattern.size()]);
        log.debug("array : {}", array);

        return (webSecurity) -> webSecurity.ignoring()
                .requestMatchers(array);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authz) -> authz
                        .anyRequest().authenticated()
                ).httpBasic(withDefaults());

        return http.build();
    }

    public static void main(String args[]) {
        //List<String> excludedPattern = SecureUtil.getNotEssentialRequestPatternList();
        //excludedPattern.addAll(SecureUtil.getStaticResourceRequestPatternList());
        //String[] array = excludedPattern.toArray(new String[excludedPattern.size()]);
        //log.debug("array : {}", SecureUtil.getNotEssentialRequestPatternsArray().toString());

    }
    
     */
}
