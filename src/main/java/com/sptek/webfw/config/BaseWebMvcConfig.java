package com.sptek.webfw.config;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sptek.webfw.argumentResolver.ArgumentResolverForMyUser;
import com.sptek.webfw.support.XssProtectSupport;
import jakarta.servlet.MultipartConfigElement;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.unit.DataSize;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Configuration
//@EnableWebMvc
public class BaseWebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private ApplicationContext applicationContext;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //js.css 변경에 따른 deploy시 파일 이름을 변경하게 됨으로 관련 maxAge를 길게 가져가도 될듯
        CacheControl cacheControl = CacheControl.maxAge(Duration.ofDays(365)).cachePublic();

        //html등에서 resource 위치를 축약해서 사용할수 있게 해준다.
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/resources/webjars/");

        //todo: css 파일의 케싱이 정확히 되지않은 이유 확인 필요
        registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/").setCacheControl(cacheControl);
        registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/").setCacheControl(cacheControl);
        registry.addResourceHandler("/images/**").addResourceLocations("classpath:/static/images/").setCacheControl(cacheControl);
    }

    //실제 viewcontroller를 만들지 않고도 간단한 역할을 수행함
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        //swagger.
        registry.addRedirectViewController("/api/demo-ui.html", "/demo-ui.html");

        //별도 컨트럴러 매핑 없이 view로 넘어가도록 설정
        registry.addViewController("/").setViewName("/pages/example/test/none");
        registry.addViewController("/none").setViewName("/pages/example/test/none");
        registry.addViewController("/temporaryParkingPageForXXX").setViewName("/pages/example/test/temporaryParkingView");
        registry.addViewController("/sorry").setViewName("/pages/example/test/temporaryParkingView");
        registry.addViewController("/fileUpload").setViewName("/pages/example/test/fileUpload");
        registry.addViewController("/pageWithPost").setViewName("/pages/example/test/pageWithPost");
        registry.addViewController("/apiWithAjax").setViewName("/pages/example/test/apiWithAjax");

    }
    
    /*
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        //thymeleaf 설정을 application.yml 에서 설정하고 있어서 사용하지 않도록 처리됨
    
        //jsp로 설정이 필요한 경우
        registry.jsp("/WEB-INF/views/", ".jsp");
        WebMvcConfigurer.super.configureViewResolvers(registry);
    }
    */

    @Bean
    public ObjectMapper objectMapper() {
        //MessageConverter 에 활용하기 위한 objectMapper를 생성, locale, timeZone등 공통요소에 대한 setting을 할수 있다.
        
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setLocale(Locale.KOREA);
        objectMapper.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

        objectMapper.getFactory().setCharacterEscapes(new XssProtectSupport()); //Xss 방지 적용
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return objectMapper;
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        //req, res 에서 message Convert로 활용하기 위한 Convertor.
        
        List<MediaType> supportedMediaTypes = new ArrayList<>();
        supportedMediaTypes.add(APPLICATION_JSON);

        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper());
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(supportedMediaTypes);
        mappingJackson2HttpMessageConverter.setPrettyPrint(true);
        return mappingJackson2HttpMessageConverter;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        //framework에서 default로 사용한 messageConvertor 설정
        
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
        stringHttpMessageConverter.setDefaultCharset(StandardCharsets.UTF_8);

        converters.add(stringHttpMessageConverter);
        converters.add(mappingJackson2HttpMessageConverter());

        WebMvcConfigurer.super.configureMessageConverters(converters);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        //controller에서 request 데이터를 object로 바인딩 해줄때 단순 바인딩이 아니라 HandlerMethodArgumentResolver를 구현한것들이 있으면 그에 따라 처리해줌.
        //HandlerMethodArgumentResolver를 구현해논 객체를 미리 등록해 둔다.
        resolvers.add(new ArgumentResolverForMyUser());
        WebMvcConfigurer.super.addArgumentResolvers(resolvers);
    }

    //Multipart 파일을 다루기 위한 Resolver
    @Bean(name = "multipartResolver")
    public StandardServletMultipartResolver multipartResolver() {
        StandardServletMultipartResolver multipartResolver = new StandardServletMultipartResolver();
        return multipartResolver;
    }

    //Multipart config 설정
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        long maxUploadSize = 10 * 1024 * 1024; //10M
        long maxUploadSizePerFile = 50 * 1024 * 1024; //50M

        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxRequestSize(DataSize.ofBytes(maxUploadSize));
        factory.setMaxFileSize(DataSize.ofBytes(maxUploadSizePerFile));

        return factory.createMultipartConfig();
    }

    @Bean
    public PlatformTransactionManager transactionManager(@Qualifier("dataSource") DataSource dataSource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource);
        transactionManager.setGlobalRollbackOnParticipationFailure(false); //TODO : false 의미 정확히 판단 필요
        return transactionManager;
    }

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setConfigLocation(this.applicationContext.getResources("classpath*:/**/mapper/*config.xml")[0]);

        //위 config.xml 을 통한 설정이 아니라 코딩으로 설정 가능
        //org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        //configuration.setMapUnderscoreToCamelCase(true);
        //configuration.setJdbcTypeForNull(JdbcType.NULL);
        //sessionFactoryBean.setConfiguration(configuration);

        sessionFactoryBean.setMapperLocations(this.applicationContext.getResources("classpath*:/**/mapper/*Mapper.xml"));
        return sessionFactoryBean.getObject();
    }

    @Bean(name = "sqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory);
        return sqlSessionTemplate;
    }
}
