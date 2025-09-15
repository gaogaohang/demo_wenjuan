package com.couple.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 情侣互动平台主应用程序
 * 
 * @author CoupleTeam
 * @version 1.0.0
 */
@SpringBootApplication
@EnableAsync
@EnableTransactionManagement
public class CoupleApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoupleApplication.class, args);
        System.out.println("=== 情侣互动平台启动成功 ===");
        System.out.println("API文档地址: http://localhost:8080/swagger-ui.html");
        System.out.println("应用监控地址: http://localhost:8080/actuator/health");
    }
}