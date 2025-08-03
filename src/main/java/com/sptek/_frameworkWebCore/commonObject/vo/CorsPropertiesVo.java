package com.sptek._frameworkWebCore.commonObject.vo;

import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import com.sptek._frameworkWebCore.util.LoggingUtil;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Data
@Component("corsPropertiesVo")
@ConfigurationProperties(prefix = "cors.options")
public class CorsPropertiesVo {
    private String defaultAccessControlAllowOrigin;
    private List<String> accessControlAllowOrigins;
    private String accessControlAllowMethods;
    private String accessControlAllowCredentials;
    private String accessControlMaxAge;
    private String accessControlAllowHeaders;

    @PostConstruct
    public void init() {
        log.info(CommonConstants.SERVER_INITIALIZATION_MARK + this.getClass().getSimpleName() + " is Applied.");
        log.info(LoggingUtil.makeFwLogForm("CORS Policy Properties"
                ,"defaultAccessControlAllowOrigin: " + accessControlAllowCredentials + "\n"
                        + "accessControlAllowOrigins: " + accessControlAllowOrigins +"\n"
                        + "accessControlAllowMethods: " + accessControlAllowMethods +"\n"
                        + "accessControlAllowCredentials: " + accessControlAllowCredentials +"\n"
                        + "accessControlMaxAge: " + accessControlMaxAge +"\n"
                        + "accessControlAllowHeaders: " + accessControlAllowHeaders
        ));
    }
}