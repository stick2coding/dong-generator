package com.dong.web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "generator")
public class GeneratorConfig {

    public boolean downloadCacheEnable;


}
