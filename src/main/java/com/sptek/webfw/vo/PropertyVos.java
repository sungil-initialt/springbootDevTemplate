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

    //프로젝트에서 자주 사용되는 프로퍼티 정보를 아래와 같이 클레스를 구성하여 필요할때 @Autowired하여 사용하면 된다.
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

    //프로퍼티 클레스를 계속 추가해 나가면 된다..
}
