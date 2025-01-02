package com.sptek.webfw.config.encryption;

import com.sptek.webfw.util.SpringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JasyptConfig {
    // 정적 데이터를 암복호 할수 있는 Encryption 으로 주로 코드내(property) 주요 정보를 암복호화 할때 사용
    // 간단한 정적 데이터 암호화에 적합함으로 실시간성 데이터 암복호에는 성능상 적합하지 않음
    // PBE(passwordBasedEncryption) 방식으로 암호화 과정에 주어진 password 값을 활용함 (password 외 랜덤 salt 값과 이에 대한 hashing 방복을 통해 보안을 높임)
    
    //--> 여기 내용 정리 필요함, 상세 설명과.. 알고리즘 업데이트등.. 추가로 웹상에서 사용할수 있는 DES, AES 코드 추가하자!

    @DependsOn({"SpringUtil"}) //SpringUtil 기능을 사용하고 있기 때문에
    @Primary
    @Bean(name = "jasyptStringEncryptor")
    public StringEncryptor stringEncryptor() {
        String pbePassword = Optional.ofNullable(System.getenv("JASYPT_ENCRYPTOR_PBEPASSWORD")).map(String::trim)
                    .orElse(SpringUtil.getProperty("jasypt.encryptor.PBEpassword", ""));

        String pbeAlgorithm = SpringUtil.getProperty("jasypt.encryptor.PBEalgorithm", "PBEWITHHMACSHA512ANDAES_256");
        log.debug("JasyptConfig : PBEpassword({}) PBEalgorithm({})", pbePassword, pbeAlgorithm);

        if(!StringUtils.hasText(pbePassword)) {
            log.error(">>#### Secure Notice : JASYPT_ENCRYPTOR_PBEPASSWORD 시스템 설정이 필요 합니다.");
            throw new IllegalStateException(String.format("Required configuration value is missing: JASYPT_ENCRYPTOR_PBEPASSWORD = %s", pbePassword));
        }
        PooledPBEStringEncryptor pooledPBEStringEncryptor = new PooledPBEStringEncryptor();

        SimpleStringPBEConfig simpleStringPBEConfig = new SimpleStringPBEConfig();
        simpleStringPBEConfig.setPassword(pbePassword); // 암호화에 사용할 대칭키
        simpleStringPBEConfig.setAlgorithm(pbeAlgorithm); // 사용할 알고리즘
        simpleStringPBEConfig.setKeyObtentionIterations("10000"); // 복호화 어렵게 하기 위해 해싱을 몇번 돌릴지의 설정 5000 이상 권장(늘릴수록 시간이 늘어남으로 적절히 조절)
        simpleStringPBEConfig.setPoolSize("1"); // 해당 모듈의 pool로 디볼트1, 멀티스레드 환경에서는 늘릴수 있음
        simpleStringPBEConfig.setProviderName("SunJCE"); // 암호화 제공자 설정, 비 설정시 디폴트 설정으로 선택됨
        simpleStringPBEConfig.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator"); // salt 값을 만들어줄 모듈
        simpleStringPBEConfig.setStringOutputType("base64"); // 최종 결과값 출력 인코딩
        pooledPBEStringEncryptor.setConfig(simpleStringPBEConfig);

        //최종 인코딩된 암호값에는 알고리즘정보 및 salt가 포함됨(salt가 포함됨으로 별도로 salt를 관리할 필요가 없음, salt가 노출된다고 해도 암호를 풀 방법이 쉬워질건 없음)
        return pooledPBEStringEncryptor;
    }
}
