package com.quantil.cm.feedback.aop;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AOPConfig {

    private static Logger logger = LoggerFactory.getLogger(AOPConfig.class);

    @Pointcut("execution(public * com.quantil.cm.feedback.service.*.*(..))")
    public void service(){}

    /**
     * service执行切面
     * @param throwable
     */
    @AfterThrowing(value = "service()",throwing = "throwable")
    public void exceptionHandler(Throwable throwable) {
        logger.error("error!",throwable);
    }
}
