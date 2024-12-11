package com.sptek.webfw.config.encryption;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Slf4j
@Configuration
public class EncryptionConfig {
    @Bean
    //spring-security 에서 비밀번호 암복호 용
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
