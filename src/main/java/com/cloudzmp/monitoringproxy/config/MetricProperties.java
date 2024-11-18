package com.cloudzmp.monitoringproxy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "zcp")
public class MetricProperties {
    private List<String> metrics;
    private List<String> endpoints;

}
