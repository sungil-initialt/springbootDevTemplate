package com.sptek.webfw.config;

import com.sptek.webfw.vo.PropertyVos;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration //swagger 관련 Bean 의 설정 (기본페스 : http://localhost:8080/swagger-ui.html)
public class SwaggerOpenApiConfig {

    @Autowired
    private PropertyVos.ProjectInfoVo projectInfoVo;

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
