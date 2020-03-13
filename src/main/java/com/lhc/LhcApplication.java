package com.lhc;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * 框架模版类
 */
@MapperScan("com.lhc.dao")
@SpringBootApplication
public class LhcApplication {

    public static void main(String[] args) {
        SpringApplication.run(LhcApplication.class, args);
    }

}
