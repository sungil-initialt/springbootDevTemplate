package com.sptek.webfw.config;

import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

@Slf4j
@Configuration
public class JasyptConfig {

    @Autowired
    private Environment environment;
    private String jasyptSecretKey = System.getenv("JASYPT_SECRET_KEY");

    @Bean("jasyptStringEncryptor")
    public StringEncryptor stringEncryptor() {

        log.info("JASYPT_SECRET_KEY(system env): {}", jasyptSecretKey);
        if(StringUtils.isEmpty(jasyptSecretKey)) {
            jasyptSecretKey = environment.getProperty("jasypt.encryptor.secretKey");
            log.info("JASYPT_SECRET_KEY(system env): is not found and be injected from app properties ({})", jasyptSecretKey);
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
