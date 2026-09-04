package com.paton;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@MapperScan("com.paton.mapper")
@ServletComponentScan
public class PatonApplication {

    public static void main(String[] args) {
        SpringApplication.run(PatonApplication.class, args);
    }
}
