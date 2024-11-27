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

}
