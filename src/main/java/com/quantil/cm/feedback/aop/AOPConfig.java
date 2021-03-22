package com.quantil.cm.feedback.aop;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * AOP 配置
 */
@Aspect
@Component
public class AOPConfig {

    private static Logger logger = LoggerFactory.getLogger(AOPConfig.class);

    /**
     * service切点
     */
    @Pointcut("execution(public * com.quantil.cm.feedback.service.*.*(..))")
    public void service(){}

    /**
     * qtl-cm-feedback不是web应用,请求不是从web容器过来<br>
     * 所以无法使用@ControllerAdvice来进行全局异常捕获<br>
     * 所以这里使用AOP的方式来实现
     * @param throwable
     */
    @AfterThrowing(value = "service()",throwing = "throwable")
    public void exceptionHandler(Throwable throwable) {
        logger.error("error!",throwable);
    }
}
