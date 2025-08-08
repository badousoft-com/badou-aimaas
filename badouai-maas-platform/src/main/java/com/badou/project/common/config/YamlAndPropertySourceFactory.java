package com.badou.project.common.config;

/**
 * @ClassName YamlAndPropertySourceFactory
 * @Description yaml文件转换器
 * @date 2022/9/20 15:05
 * @Version 1.0
 */

import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class YamlAndPropertySourceFactory extends DefaultPropertySourceFactory {
    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        if (resource == null) {
            return super.createPropertySource(name, resource);
        }
        Resource resourceResource = resource.getResource();
        if (!resourceResource.exists()) {
            return new PropertiesPropertySource(null, new Properties());
        } else if (resourceResource.getFilename().endsWith(".yml") || resourceResource.getFilename().endsWith(".yaml")) {
            List<PropertySource<?>> sources = new YamlPropertySourceLoader().load(resourceResource.getFilename(), resourceResource);
            return sources.get(0);
        }
        return super.createPropertySource(name, resource);
    }
}
