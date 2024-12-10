package com.sptek.webfw.config.encryption;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JasyptConfig {

    private final Environment environment;
    private String jasyptSecretKey = System.getenv("JASYPT_SECRET_KEY");

    @Bean("jasyptStringEncryptor")
    public StringEncryptor stringEncryptor() {

        if(!StringUtils.isEmpty(jasyptSecretKey)) {
            log.info(">> set JASYPT_SECRET_KEY from system environment");

        } else {
            //jasypt를 위한 secretKey 값이 시스템 환경값으로 셋팅이 안된경우 property 에서 읽어서 처리함
            jasyptSecretKey = environment.getProperty("jasypt.encryptor.secretKey");
            log.info(">> set JASYPT_SECRET_KEY from property");
        }

        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();

        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(jasyptSecretKey);
        config.setAlgorithm("PBEWITHMD5ANDDES");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);

        return encryptor;
    }
}
