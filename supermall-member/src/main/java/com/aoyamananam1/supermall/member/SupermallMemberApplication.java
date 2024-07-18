package com.aoyamananam1.supermall.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.aoyamananam1.supermall.member.feign")
public class SupermallMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupermallMemberApplication.class, args);
    }

}
