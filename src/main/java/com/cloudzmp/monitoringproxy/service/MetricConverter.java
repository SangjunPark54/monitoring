package com.cloudzmp.monitoringproxy.service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.cloudzmp.monitoringproxy.config.MetricProperties;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MetricConverter {

    private final List<String> allowedMetrics;
    private final List<String> endpointList;
    private final RestTemplate restTemplate;
    private final List<String> kubernetesEndpointList;

    public MetricConverter(MetricProperties metricProperties, RestTemplateBuilder restTemplate) {
        this.allowedMetrics = metricProperties.getMetrics();
        this.endpointList = metricProperties.getEndpoints();
        this.restTemplate = restTemplate.build();
        this.kubernetesEndpointList = metricProperties.getKubernetesEndpoint();
    }

    public List<String> getAllMetrics() {
        log.info("Start getAllMetrics");
        List<String> result = endpointList.parallelStream()
                .map(this::collectRawMetric)
                .map(this::getAllowedMetrics)
                .flatMap(List::stream)
                .toList();
        log.info("Done getAllMetrics");
        return result;
    }

    public String collectRawMetric(String svcName) {
        try {
            log.info("collecing raw metric start");
            String result = restTemplate.getForObject(svcName, String.class);
//            List<String> filtered = Arrays.stream(result.split("\n")).parallel()
//                    .filter(line -> !line.startsWith("#")&&!line.endsWith("."))
//                    .toList();

            log.info("collecing raw metric end");
            return result;
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to collect metric", e);
        }
    }

//    private List<String> getAllowedMetrics(String rawMetrics) {
//        log.info("split lines..");
//        List<String> result = Arrays.stream(rawMetrics.split("\n")).parallel()
//                .filter(raw -> allowedMetrics.parallelStream().anyMatch(raw::contains))
//                .collect(Collectors.toList());
//        log.info("Successfully scrape Metrics");
//        log.debug("raw:{}, filtered: {}", rawMetrics, result);
//        return result;
//    }


    private List<String> getAllowedMetrics(String rawMetrics) {
        log.info("start getAllowedMetrics (split + fileter)");
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            return executor.submit(() ->
                    Arrays.stream(rawMetrics.split("\n"))
                            .parallel()
                            .filter(raw -> allowedMetrics.stream().anyMatch(raw::contains))
                            .collect(Collectors.toList())
            ).get();
        } catch (ExecutionException e) {
            throw new RuntimeException("Error while processing metrics", e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String collectMetrics() throws Exception {

        // Bearer 토큰 파일 경로
        String tokenPath = "/var/run/secrets/kubernetes.io/serviceaccount/token";
        String caCertPath = "/var/run/secrets/kubernetes.io/serviceaccount/ca.crt";

        // Bearer 토큰 읽기
        String bearerToken = new String(Files.readAllBytes(Paths.get(tokenPath)));

        // HTTP 헤더 설정 (Bearer 토큰 포함)
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + bearerToken);

        // HTTP 엔티티 생성
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // HTTPS 요청 (TLS 인증서 사용)
        ResponseEntity<String> response = restTemplate.exchange(
                kubernetesEndpointList.get(0),
                HttpMethod.GET,
                entity,
                String.class
        );

        // 응답 본문 반환 (메트릭 데이터)
        return response.getBody();
    }
}
