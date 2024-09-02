package com.aoyamananam1.supermall.product.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;


@Configuration
public class MyThreadConfig {

    @Bean
    public ThreadPoolExecutor executor(ThreadPoolConfigProperties properties){
        return new ThreadPoolExecutor(
                properties.getCoreSize(),
                properties.getMaxSize(),
                properties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(100000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }
}
