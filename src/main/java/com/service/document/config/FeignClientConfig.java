package com.service.document.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "app.services.storage.enabled", havingValue = "true", matchIfMissing = false)
@EnableFeignClients(basePackages = "com.service.document.client")
public class FeignClientConfig {
    // Configuration for Feign clients
}