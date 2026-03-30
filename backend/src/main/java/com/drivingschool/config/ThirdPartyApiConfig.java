package com.drivingschool.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 第三方API配置类
 */
@Configuration
public class ThirdPartyApiConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}