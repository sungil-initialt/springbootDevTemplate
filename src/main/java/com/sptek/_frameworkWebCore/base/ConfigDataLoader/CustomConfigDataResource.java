package com.sptek._frameworkWebCore.base.ConfigDataLoader;

import org.springframework.boot.context.config.ConfigDataResource;

public class CustomConfigDataResource extends ConfigDataResource {

    private final String locationPattern;

    public CustomConfigDataResource(String locationPattern) {
        this.locationPattern = locationPattern;
    }

    public String getLocationPattern() {
        return locationPattern;
    }
}