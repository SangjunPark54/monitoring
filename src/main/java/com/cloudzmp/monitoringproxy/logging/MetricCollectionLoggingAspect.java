package com.cloudzmp.monitoringproxy.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class MetricCollectionLoggingAspect {

    @Around("@annotation(LogMetricCollection), execution(* com.cloudzmp.monitoringproxy.service.MetricCollector.*(..))")
    public Object logMetricCollection(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;
            log.info("Collecting metrics is done : {}ms", duration);
            return result;
        } catch (Exception e) {
            log.error("Failed to Collect metrics : {}, duration : {}ms",
                    e.getMessage(),
                    System.currentTimeMillis() - startTime);
            throw e;
        }
    }
}
