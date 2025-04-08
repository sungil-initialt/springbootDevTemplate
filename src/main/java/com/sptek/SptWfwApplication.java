package com.sptek;

import com.sptek._frameworkWebCore.annotation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@Slf4j
@SpringBootApplication
@ServletComponentScan //필터쪽에 @WebFilter 를 사용하기 위해 필요함
@TestAnnotation_InAll

//@EnableResponseOfApplicationGlobalException_InMain
@EnableMdcTagging_InMain
@EnableDetailLog_InMain_Controller_ControllerMethod

//---------------------------------------------
@EnableDatasourceOfH2_InMain
//@EnableDatasourceOfMysqlReplication_InMain
//@EnableDatasourceOfMysqlReplicationWithJndi_InMain
//---------------------------------------------
@EnableJpaHybrid_InMain

@EnableConsoleLogEnvironmentProperties_InMain
@EnableCorsPolicyFilter_InMain
@EnableEncryptorJasypt_InMain
@EnableThymeleafSpringSecurityDialect_InMain //thymeleaf 에서 springSecurity 요소를 사용 하기 위한 설정
@EnableNoFilterAndSessionForMinorRequest_InMain //minor 한 request 에 대해 session 생성 방지 (세션 관리 효율)
@EnableHttpCachePublicForStaticResource_InMain
@EnableGlobalViewModelForStatic_InMain
@EnableGlobalViewModelForUserAuthentication_InMain
@EnableResponseOfApplicationGlobalException_InMain
//@EnableResponseOfDevViewGlobalException_InMain

public class SptWfwApplication {
	public static void main(String[] args) {
		SpringApplication.run(SptWfwApplication.class, args);

		//아래와 같이 프로파일을 지정(추가)할 수 있지만 환경변수 또는 실행파람의 프로파일에 비해 우선순위가 낮다.
		//다시말해 app.setAdditionalProfiles("dev, stg") 같이 dev, stg 를 추가 하여도 프로파일에 목록에는 올라가지만 실제 환경변수에 다른 프로파일이 있다면 해당 프로파일의 프로퍼티파일을 로딩한다.
		//환경변수에 별도 프로파일이 없으면 dev, stg가 프로파일로 올라가지만 프로퍼티 파일은 stg로 올라간다(순서상 뒷쪽)
		//그럼에도 여러 프로파일을 사용하는 이유는 프로파일에 따라서 특정 Bean을 생성할지 말지를 선택적으로 적용할수 있기 때문이다.
		//SpringApplication app = new SpringApplication(SptWfwApplication.class);
		//app.setAdditionalProfiles("dev, stg");
		//app.run(args);
	}
}
