package com.sptek._frameworkWebCore.external;

import com.sptek._frameworkWebCore.commonObject.vo.ProjectInfoVo;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
//@Profile(value = {"local", "dev", "stg"})
//@HasAnnotationOnMain_InBean(EnableSwaggerOpenApi_InMain.class)
//@EnableConfigurationProperties(ProjectInfoVo.class) //ProjectInfoVo 가 프로필에 따라 오류가 나서..
@RequiredArgsConstructor
@Configuration
public class SwaggerOpenApiConfig {

    private final ProjectInfoVo projectInfoVo;

    // @Profile 등의 설정으로 해당 빈이 로딩이 되지 않더라도 swagger 가 최초 로딩한 디폴트 객체를 사용하기 때문에 @Profile 설정 등으로 on/off 가 되지 않음
    // todo: 오직 pringdoc.api-docs.enabled : false 설정으로만 꺼지는 듯..
    @Bean
    public OpenAPI openAPI() {
        //default url : http://localhost:8080/swagger-ui.html

        Info info = new Info()
                .title(projectInfoVo.getName())
                .version(projectInfoVo.getVersion())
                .description(projectInfoVo.getDescription());
        return new OpenAPI().components(new Components()).info(info);
    }
}
