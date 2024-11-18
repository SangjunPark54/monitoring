//package com.cloudzmp.monitoringproxy.component;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.util.List;
//import java.util.Objects;
//
//import org.springframework.stereotype.Component;
//
//import com.cloudzmp.monitoringproxy.model.Metrics;
//
//import jakarta.annotation.PostConstruct;
//
//@Component
//public class MetricFilter {
//
//    @PostConstruct
//    public void loadAllowedMetrics(Metrics allowedMetrics) throws Exception {
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
//                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("metrics.yaml"))))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                allowedMetrics.add(line.trim());
//            }
//        }
//    }
//
//    public boolean isMetricAllowed(String metricName) {
//        return allowedMetrics.contains(metricName);
//    }
//}
