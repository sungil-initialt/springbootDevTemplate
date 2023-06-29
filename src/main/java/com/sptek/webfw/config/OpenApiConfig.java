package com.sptek.webfw.config;

import com.sptek.webfw.config.property.PropertyVos;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Autowired
    private PropertyVos.ProjectInfo projectInfo;

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title(projectInfo.getName())
                .version(projectInfo.getVersion())
                .description(projectInfo.getDescription());

        return new OpenAPI()
                .components(new Components())
                .info(info);
    }
}
