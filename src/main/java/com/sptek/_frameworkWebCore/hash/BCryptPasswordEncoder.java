package com.sptek._frameworkWebCore.hash;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class BCryptPasswordEncoder {
    @Bean
    public org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder bCryptPasswordEncoder() {
        // springframework 에서 제공 하는 BCrypt 알고리즘을 사용하는 비밀번호 해시용 클레스
        // spring-security 등 에서 비밀번호 해싱 매칭용으로 사용
        // 랜덤 salt 값을 사용하며 salt값이 인코딩 결과값에 포함됨으로 따로 저장할 필요가 없음
        // 해싱 결과값은 (알고리즘버전 + 강도 + 사용된랜덤salt값 + 해싱된원문최종결과) 로 구성됨

        // 보안 강도 default = 10
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder(10);
    }
}
