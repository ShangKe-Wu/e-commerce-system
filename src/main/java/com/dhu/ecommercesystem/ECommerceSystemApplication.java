package com.dhu.ecommercesystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.dhu.ecommercesystem.mapper")
public class ECommerceSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ECommerceSystemApplication.class, args);
    }

}
