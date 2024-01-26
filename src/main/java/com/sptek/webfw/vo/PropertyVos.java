package com.sptek.webfw.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
/*
@ConfigurationProperties 선언을 통해 Property 파일에서 해달 필드를 자동 주입 받음
 */
@Component
public class PropertyVos {

    @Data
    @Component
    @ConfigurationProperties(prefix = "project-info") //property에서 해당 prefix 값 이하의 항목과 매핑해 준다.
    public class ProjectInfoVo {

        @Schema(description = "프로젝트 이름")
        private String name;
        @Schema(description = "프로젝트 버전")
        private String version;
        @Schema(description = "프로젝트 설명")
        private String description;
    }
}
