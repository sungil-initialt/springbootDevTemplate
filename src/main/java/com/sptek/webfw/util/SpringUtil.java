package com.sptek.webfw.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class SpringUtil {
    private static Environment environment;

    @Autowired
    public SpringUtil(Environment environment) {
        SpringUtil.environment = environment;
    }

    public static String getProperty(String key) {
        return SpringUtil.environment.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return SpringUtil.environment.getProperty(key, defaultValue);
    }
}
