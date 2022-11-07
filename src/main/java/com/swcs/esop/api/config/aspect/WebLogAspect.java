package com.swcs.esop.api.config.aspect;

import com.swcs.esop.api.util.TimeUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;


/**
 * @author 阮程
 * @date 2017年11月10日
 */
@Aspect
@Configuration
public class WebLogAspect {

    private Logger logger = LoggerFactory.getLogger(WebLogAspect.class);

    @Pointcut("execution (public * com.swcs.esop.api.web.controller..*.*(..))")
    @Order(10)
    public void webLog() {
    }


    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) {
        String name = getClass().getSimpleName();
        TimeUtil timeUtil = TimeUtil.start(name);
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        String method = request.getMethod();

        StringBuilder sb = new StringBuilder();
        sb.append("[").append(request.getRequestURL().toString()).append("] ");
        sb.append("[").append(method).append("] ");
        sb.append("[").append(request.getRemoteAddr()).append("] ");
        sb.append("[").append(joinPoint.getSignature().getDeclaringTypeName()).append(".").append(joinPoint.getSignature().getName()).append("] ");

        timeUtil.setMsg(sb.toString());
    }

    @AfterReturning(returning = "ret", pointcut = "webLog()")
    public void doAfterReturning(Object ret) {
        String name = getClass().getSimpleName();
        TimeUtil timeUtil = TimeUtil.end(name);
        String msg = timeUtil.getMsg() + "[" + Long.toString(TimeUtil.getCost(name)) + "ms]";
        logger.info(msg);
    }
}
