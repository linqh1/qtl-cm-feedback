package com.quantil.cm.feedback;

import org.apache.rocketmq.client.log.ClientLogger;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
@MapperScan(basePackages = "com.quantil.cm.feedback.mapper")
public class FeedbackMain {

    public static void main(String[] args) throws Exception {
        System.setProperty(ClientLogger.CLIENT_LOG_USESLF4J, "true");
        SpringApplication.run(FeedbackMain.class, args);
    }
}
