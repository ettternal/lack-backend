package com.whale.lack;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动类
 */
@SpringBootApplication
@MapperScan("com.whale.lack.mapper")
@EnableScheduling //引入任务框架，支持定时任务
public class LackApplication {

    public static void main(String[] args) {
        SpringApplication.run(LackApplication.class, args);
    }

}

