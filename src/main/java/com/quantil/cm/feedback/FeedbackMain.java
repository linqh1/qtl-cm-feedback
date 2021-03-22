package com.quantil.cm.feedback;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
@MapperScan(basePackages = "com.quantil.cm.feedback.mapper")
public class FeedbackMain {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(FeedbackMain.class, args);
    }
}
