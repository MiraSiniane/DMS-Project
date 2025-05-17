package com.service.document.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.service.document.client")
public class FeignClientConfig {
    // Configuration for Feign clients
}
