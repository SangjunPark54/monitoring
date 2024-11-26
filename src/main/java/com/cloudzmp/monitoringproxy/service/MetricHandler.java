package com.cloudzmp.monitoringproxy.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MetricHandler {

    private final MetricCollector metricCollector;

    public MetricHandler(MetricCollector metricCollector) {
        this.metricCollector = metricCollector;
    }

    public List<String> collectMetricsList() {
        return metricCollector.getAllMetrics();
    }

    public String getKubeStateMetrics() {
        return metricCollector.getKubeStateMetrics();
    }

    public String getOssMetricsFromEndpoint() {
        return metricCollector.getOssMetricsFromEndpoint();
    }

    public String getMetrics() {
        return metricCollector.getMetrics();
    }

    public String getKubeNodeMetrics() {
        return metricCollector.getNodeProxyMetrics();
    }

    public String getNodenameProxyMetrics() {
        return metricCollector.getNodenameProxyMetrics();
    }
}
