package com.bike;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.bike.mapper")
public class BikeApplication {
    public static void main(String[] args) {
        SpringApplication.run(BikeApplication.class, args);
        System.out.println("===============================================");
        System.out.println("  城市单车骑行补给站点台账系统 启动成功!");
        System.out.println("  后端接口地址: http://localhost:8081");
        System.out.println("  接口文档地址: http://localhost:8081/doc.html");
        System.out.println("===============================================");
    }
}
