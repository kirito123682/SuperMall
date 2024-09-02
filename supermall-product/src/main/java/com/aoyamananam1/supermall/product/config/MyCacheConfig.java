package com.aoyamananam1.supermall.product.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
@EnableConfigurationProperties(CacheProperties.class)
public class MyCacheConfig {

    @Bean
    RedisCacheConfiguration redisCacheConfiguration(CacheProperties cacheProperties){

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();

        config = config.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));
        config = config.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericFastJsonRedisSerializer()));

        //将配置文件中的配置也生效
        CacheProperties.Redis redis = cacheProperties.getRedis();

        if (redis.getTimeToLive() != null){
            config = config.entryTtl(redis.getTimeToLive());
        }
        if (redis.getKeyPrefix() != null){
            config = config.prefixCacheNameWith(redis.getKeyPrefix());
        }
        if (!redis.isCacheNullValues()){
            config = config.disableCachingNullValues();
        }
        if (!redis.isUseKeyPrefix()){
            config = config.disableKeyPrefix();
        }
        return config;
    }
}
