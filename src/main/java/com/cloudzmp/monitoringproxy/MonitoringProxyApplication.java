package com.cloudzmp.monitoringproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

import com.cloudzmp.monitoringproxy.config.MetricProperties;

@ConfigurationPropertiesScan
@SpringBootApplication
@EnableAsync
public class MonitoringProxyApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonitoringProxyApplication.class, args);
    }

}
