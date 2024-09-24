package com.aoyamananam1.supermall.cart.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@EnableRedisHttpSession
@Configuration
public class SupermallSessionConfig {

//    @Bean
//    public WebSessionIdResolver webSessionIdResolver(){
//        CookieWebSessionIdResolver resolver = new CookieWebSessionIdResolver();
//        resolver.setCookieName("SUPERSESSION");
////
//        resolver.addCookieInitializer((builder) -> builder.path("/"));
////        resolver.addCookieInitializer((builder) -> builder.sameSite("Strict"));
//        resolver.addCookieInitializer((builder) -> builder.domain("supermall00.com"));
//        return resolver;
//    }

    @Bean
    public CookieSerializer cookieSerializer(){
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        cookieSerializer.setDomainName("supermall00.com");
        cookieSerializer.setCookieName("SUPERSESSION");
        return cookieSerializer;
    }

    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer(){
        return new GenericFastJsonRedisSerializer();
    }

}
