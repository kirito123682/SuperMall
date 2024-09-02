package com.aoyamananam1.supermall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 整合mybatis-plus
 * 		1依赖
 * 		2配置
 * 			2.1配置数据源
 * 				驱动
 * 			2.2配置mp
 */
@EnableFeignClients(basePackages = "com.aoyamananam1.supermall.product.feign")
@EnableDiscoveryClient
@MapperScan("com.aoyamananam1.supermall.product.dao")
@SpringBootApplication
public class SupermallProductApplication {

	public static void main(String[] args) {
		SpringApplication.run(SupermallProductApplication.class, args);
	}

}
