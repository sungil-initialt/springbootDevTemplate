package com.sptek._frameworkWebCore.config.external;

import com.sptek._frameworkWebCore.vo.PropertyVos;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration //swagger 관련 Bean 의 설정 (기본페스 : http://localhost:8080/swagger-ui.html)
public class SwaggerOpenApiConfig {

    private final PropertyVos.ProjectInfoVo projectInfoVo;

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title(projectInfoVo.getName())
                .version(projectInfoVo.getVersion())
                .description(projectInfoVo.getDescription());

        return new OpenAPI()
                .components(new Components())
                .info(info);
    }
}
