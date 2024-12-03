package com.cloudzmp.metricagent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

import com.cloudzmp.metricagent.config.MetricProperties;

@ConfigurationPropertiesScan
@SpringBootApplication
@EnableAsync
@EnableConfigurationProperties(MetricProperties.class)
public class MetricAgent {

    public static void main(String[] args) {
        SpringApplication.run(MetricAgent.class, args);
    }

}
