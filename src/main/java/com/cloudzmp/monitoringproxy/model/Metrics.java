package com.cloudzmp.monitoringproxy.model;

import java.util.List;

import lombok.Data;

@Data
public class Metrics {
    private List<String> allowedMetrics;
}
