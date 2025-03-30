package com.sptek._frameworkWebCore.encryption.encryptor;

import com.sptek._frameworkWebCore.annotation.EnableEncryptorJasypt_InMain;
import com.sptek._frameworkWebCore.annotation.annotationCondition.HasAnnotationOnMain_InBean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

@Slf4j
@RequiredArgsConstructor
@Configuration
@HasAnnotationOnMain_InBean(EnableEncryptorJasypt_InMain.class)
public class JasyptStringEncryptorConfig {
    // 정적 데이터를 암복호 할수 있는 Encryption 으로 주로 코드내(property) 주요 정보를 암복호화 할때 사용
    // 간단한 정적 데이터 암호화에 적합함으로 실시간성 데이터 암복호에는 성능상 적합하지 않음
    // PBE(passwordBasedEncryption) 방식으로 암호화 과정에 주어진 password 값을 활용함 (password 외 랜덤 salt 값과 이에 대한 hashing 반복을 통해 보안을 높임)

    final private Environment environment;

    //@Primary
    @Bean(name = "customJasyptStringEncryptor")
    public StringEncryptor stringEncryptor() {
        // todo: prd 운영시 반드시 환경 변수로 설정해야 함(보안이슈), 로그로 password 노출 하지 말것
        String pbePassword = environment.getProperty("jasypt.encryptor.password");
        String pbeAlgorithm = environment.getProperty("jasypt.encryptor.algorithm", "PBEWITHHMACSHA512ANDAES_256");
        log.debug("pbePassword({}), pbeAlgorithm({})", StringUtils.hasText(pbePassword) ? pbePassword.substring(0, pbePassword.length()/2)+"..." : "", pbeAlgorithm);

        if(!StringUtils.hasText(pbePassword)) {
            log.error(">>#### Secure Notice : JASYPT_ENCRYPTOR 의 PBE_PASSWORD 설정이 필요 합니다.");
            throw new IllegalStateException(String.format("Required configuration value is missing: PBE_PASSWORD = %s", pbePassword));
        }

        PooledPBEStringEncryptor pooledPBEStringEncryptor = new PooledPBEStringEncryptor();
        pooledPBEStringEncryptor.setConfig(getSimpleStringPBEConfig(pbePassword, pbeAlgorithm));

        //최종 인코딩된 암호값에는 알고리즘정보 및 salt가 포함됨(salt가 포함됨으로 별도로 salt를 관리할 필요가 없음, salt가 노출된다고 해도 암호를 풀 방법이 쉬워질건 없음)
        return pooledPBEStringEncryptor;
    }

    // todo: 실제 활용 에서는 보안 설정을 더 높일 것
    private static @NotNull SimpleStringPBEConfig getSimpleStringPBEConfig(String pbePassword, String pbeAlgorithm) {
        SimpleStringPBEConfig simpleStringPBEConfig = new SimpleStringPBEConfig();
        simpleStringPBEConfig.setPassword(pbePassword); // 암호화에 사용할 대칭키
        simpleStringPBEConfig.setAlgorithm(pbeAlgorithm); //사용 알고리즘

        simpleStringPBEConfig.setKeyObtentionIterations("10000"); // 복호화 어렵게 하기 위해 해싱을 몇번 돌릴지의 설정 5000 이상 권장(늘릴수록 시간이 늘어남으로 적절히 조절)
        simpleStringPBEConfig.setPoolSize("1"); // 해당 모듈의 pool로 디볼트1, 멀티스레드 환경에서는 늘릴수 있음
        simpleStringPBEConfig.setProviderName("SunJCE");
        simpleStringPBEConfig.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        simpleStringPBEConfig.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        simpleStringPBEConfig.setStringOutputType("base64"); // 최종 결과값 출력 인코딩

        return simpleStringPBEConfig;
    }
}
