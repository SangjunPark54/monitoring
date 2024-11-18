package com.cloudzmp.monitoringproxy.service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.cloudzmp.monitoringproxy.config.MetricProperties;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MetricHandler {

    private final MetricConverter metricConverter;
    private final List<String> endpointList;

    public MetricHandler(MetricConverter metricConverter, MetricProperties metricProperties) {
        this.metricConverter = metricConverter;
        this.endpointList = metricProperties.getEndpoints();
    }

    public String createMetricsFileIfNotExists() throws IOException {
        Path metricsDirPath = Paths.get("./resources/metrics/");
        Path metricsFilePath = Paths.get("index.html");
        if (!Files.exists(metricsDirPath)) {
            Files.createDirectories(metricsDirPath);  // 디렉토리 생성
        }

        if (!Files.exists(metricsFilePath)) {
            Files.createFile(metricsFilePath);  // 파일이 없을 때만 생성
        }
        return String.valueOf(metricsFilePath);
    }

    public String collectMetrics() {
        return String.join("\n", metricConverter.getAllMetrics());
    }

    public List<String> collectMetricsList() {
        return metricConverter.getAllMetrics();
    }

    public void writeMetricsToFile() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("./resources/metrics/index.html"))) {
            // 각 엔드포인트에서 메트릭을 수집하고 필터링한 후 파일에 기록
            for (String endpoint : endpointList) {
                String rawMetric = metricConverter.collectRawMetric(endpoint);

                // 필터링된 메트릭을 파일에 기록
//                for (String metric : rawMetric) {
                    writer.write(rawMetric);
                    writer.newLine();
                }
            }
            System.out.println("Metrics written to index.html");
//        } catch (IOException e) {
//            System.err.println("Error writing metrics to file: " + e.getMessage());
//        }
    }
}
