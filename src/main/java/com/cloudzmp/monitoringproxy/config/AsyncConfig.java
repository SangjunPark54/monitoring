//package com.cloudzmp.monitoringproxy.config;
//
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.core.task.AsyncTaskExecutor;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//public class AsyncConfig implements WebMvcConfigurer {
//
//    @Bean
//    public AsyncTaskExecutor taskExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(10);
//        executor.setMaxPoolSize(50);
//        executor.setQueueCapacity(100);
//        executor.setThreadNamePrefix("Async-");
//        executor.initialize();
//        return executor;
//    }
//
//    @Override
//    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
//        configurer.setTaskExecutor(taskExecutor());
//        configurer.setDefaultTimeout(30000);  // 타임아웃 설정 (30초)
//    }
//}
//
