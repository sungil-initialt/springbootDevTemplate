package com.sptek.webfw.config.property;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
public class PropertyVos {

    @Data
    @Component
    @ConfigurationProperties(prefix = "project-info")
    public class ProjectInfo {

        @Schema(description = "프로젝트 이름")
        private String name;
        @Schema(description = "프로젝트 버전")
        private String version;
        @Schema(description = "프로젝트 설명")
        private String description;
    }
}
