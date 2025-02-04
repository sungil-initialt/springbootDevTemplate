package com.sptek._frameworkWebCore.base.ConfigDataLoader;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ConfigDirectoryLoader implements EnvironmentPostProcessor {
    // application.yml 파일을 기능적으로 분리해서 등록 가능하게 해준다. (지정된 디렉토리 내부의 모든 .YML 파일을 찾아서 application.yml 파일의 일부로 인식하게 만들어 준다.)
    private static final String DEFAULT_BASE_PATH = "classpath:serviceNameResources/_global/specificPurposeProperties/";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // application.yml에서 basePath 값 읽기
        String basePath = environment.getProperty("sptFramework.specificPurposeProperties.basePath", DEFAULT_BASE_PATH);
        String configPathPattern = basePath + "/**/*.yml"; // 경로 패턴 생성

        log.info("Loading YAML configuration files from path: {}", configPathPattern);

        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Yaml yaml = new Yaml();
        Map<String, Object> mergedProperties = new ConcurrentHashMap<>(); // 스레드 안전한 자료구조 사용

        // 현재 활성화된 프로파일 가져오기
        List<String> activeProfiles = Arrays.asList(environment.getActiveProfiles());
        log.info("Active profiles: {}", activeProfiles);

        try {
            Resource[] resources = resolver.getResources(configPathPattern);
            for (Resource resource : resources) {
                String fileName = Objects.requireNonNull(resource.getFilename());

                // 기본 설정 파일 처리 (e.g., config.yml)
                if (!fileName.contains("-")) {
                    log.info("Loading default YAML config: {}", fileName);
                    loadYamlToMap(resource, yaml, mergedProperties);
                }

                // 프로파일별 설정 파일 처리 (e.g., config-local.yml)
                for (String profile : activeProfiles) {
                    if (fileName.endsWith("-" + profile + ".yml")) {
                        log.info("Loading profile-specific YAML config: {}", fileName);
                        loadYamlToMap(resource, yaml, mergedProperties);
                    }
                }
            }

            // 병합된 설정을 Spring 환경에 추가
            environment.getPropertySources().addLast(new MapPropertySource("externalYamlConfigs", mergedProperties));

        } catch (IOException e) {
            log.error("Failed to load YAML config files from path: {}", configPathPattern, e);
            throw new IllegalStateException("Could not load YAML configuration files", e); // 예외를 던져 애플리케이션 실행 중단
        }
    }

    /**
     * YAML 파일을 Map으로 로드하고 병합
     */
    private void loadYamlToMap(Resource resource, Yaml yaml, Map<String, Object> target) throws IOException {
        try (InputStream inputStream = resource.getInputStream()) {
            Map<String, Object> yamlData = yaml.load(inputStream);
            if (yamlData != null) {
                mergeMaps(target, flattenMap("", yamlData));
            }
        } catch (Exception e) {
            log.error("Failed to parse YAML file: {}", resource.getFilename(), e);
            throw new IllegalArgumentException("Invalid YAML format in file: " + resource.getFilename(), e);
        }
    }

    /**
     * 계층적 Map을 평탄화하여 key.subkey=value 형태로 변환
     */
    private Map<String, Object> flattenMap(String parentKey, Map<String, Object> source) {
        Map<String, Object> flatMap = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            String key = parentKey.isEmpty() ? entry.getKey() : parentKey + "." + entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map) {
                flatMap.putAll(flattenMap(key, (Map<String, Object>) value));
            } else {
                flatMap.put(key, value);
            }
        }
        return flatMap;
    }

    /**
     * 두 개의 Map을 병합. 기존 키가 있을 경우 경고 로그 출력.
     */
    private void mergeMaps(Map<String, Object> target, Map<String, Object> source) {
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (target.containsKey(key)) {
                log.warn("Duplicate key detected: {}. Overwriting value [{}] with [{}]", key, target.get(key), value);
            }

            target.put(key, value); // 새로운 값으로 덮어쓰기
        }
    }
}