package com.aoyamananam1.supermall.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SupermallCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupermallCouponApplication.class, args);
    }

}
