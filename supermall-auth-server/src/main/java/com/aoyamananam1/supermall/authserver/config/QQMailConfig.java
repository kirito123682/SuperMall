package com.aoyamananam1.supermall.authserver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.mail")
@Data
public class QQMailConfig {
    private String emailFrom = "1458881379@qq.com";
    private String name;
}
