package com.aoyamananam1.supermall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class MyRedissonConfig {

    @Bean(destroyMethod="shutdown")
    public RedissonClient redisson() throws IOException {
        // 创建配置
        Config config = new Config();
//        config.useClusterServers()
//                .addNodeAddress("127.0.0.1:7004", "127.0.0.1:7001");
        // 创建实例
        config.useSingleServer().setAddress("redis://127.0.0.1:6379").setPassword("123456");
        return Redisson.create(config);
    }

}
