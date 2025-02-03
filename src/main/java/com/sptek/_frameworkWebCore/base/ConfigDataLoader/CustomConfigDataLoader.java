package com.sptek._frameworkWebCore.base.ConfigDataLoader;

import org.springframework.boot.context.config.ConfigDataLoader;
import org.springframework.boot.context.config.ConfigDataLocationResolver;
import org.springframework.boot.context.config.ConfigDataResource;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


-->여기부터 해결해야 함
public class CustomConfigDataLoader implements ConfigDataLoader<CustomConfigDataResource> {

    private static final ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();

    @Override
    public List<ConfigDataResource> load(ConfigDataLocationResolver context, CustomConfigDataResource location) throws IOException {
        Resource[] resources = resourceResolver.getResources(location.getLocationPattern());
        if (resources.length == 0) {
            throw new ConfigDataResourceNotFoundException((ConfigDataResource) location, (Throwable) context);
        }
        return Stream.of(resources)
                .map(resource -> new CustomConfigDataResource("classpath:" + resource.getFilename()))
                .collect(Collectors.toList());
    }
}
