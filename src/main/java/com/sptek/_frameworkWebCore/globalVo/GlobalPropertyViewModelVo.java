package com.sptek._frameworkWebCore.globalVo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Data
@ConfigurationProperties(prefix = "global-property-view-model")
public class GlobalPropertyViewModelVo {
    private Map<String, Object> attributes = new HashMap<>();
}