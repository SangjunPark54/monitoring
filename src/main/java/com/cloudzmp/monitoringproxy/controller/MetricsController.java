package com.cloudzmp.monitoringproxy.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.cloudzmp.monitoringproxy.service.MetricHandler;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class MetricsController {

    private static final String METRICS_FILE_PATH = "./resources/metrics/index.html";
    private final MetricHandler metricHandler;

    public MetricsController(MetricHandler metricHandler) {
        this.metricHandler = metricHandler;
    }
    @GetMapping("/metrics")
    @ResponseBody
    public List<String> getFilteredMetrics() {
        return metricHandler.collectMetricsList();
    }

    @GetMapping("/zmpMetrics")
    public ResponseEntity<Object> getMetrics1() throws IOException {
        return ResponseEntity.ok(Files.readAllLines(Path.of(METRICS_FILE_PATH)));
    }

    @GetMapping("/zmpMetrics2")
    public StreamingResponseBody getMetrics2(HttpServletResponse response) {
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "inline; filename=\"metrics.txt\"");

        return outputStream -> {
            try (BufferedReader reader = Files.newBufferedReader(Paths.get(METRICS_FILE_PATH))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (response.isCommitted()) {  // 응답이 이미 완료되었는지 확인
                        break;  // 응답 완료 시 루프 중단
                    }
                    outputStream.write((line + "\n").getBytes());
                    outputStream.flush();  // 데이터 스트리밍
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    @GetMapping("/zmpMetrics3")
    public ResponseEntity<String> getMetrics() throws IOException {
        // 스트리밍 방식으로 파일을 한 줄씩 읽어와서 응답
        try (var linesStream = Files.lines(Path.of(METRICS_FILE_PATH))) {
            String result = linesStream.collect(Collectors.joining("\n"));
            return ResponseEntity.ok(result);
        }
    }

    @PostMapping("/tempPush")
    public ResponseEntity writeMetricsToFile() throws IOException {
        metricHandler.writeMetricsToFile();
        return ResponseEntity.ok().build();
    }
}
