package com.cloudzmp.monitoringproxy.controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cloudzmp.monitoringproxy.service.MetricCollector;
import com.cloudzmp.monitoringproxy.service.MetricHandler;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class MetricsController {

    private final MetricCollector service;

    public MetricsController(MetricCollector service) {
        this.service = service;
    }

    @ResponseBody
    @GetMapping("/metrics/oss/nginx")
    public String getNginxIngressMetrics() {
        return service.getNginxIngressMetrics();
    }
    @ResponseBody
    @GetMapping("/metrics/oss/es")
    public String getElasticSearchMetrics() {
        return service.getElasticSearchMetrics();
    }
    @ResponseBody
    @GetMapping("/metrics/oss/certmanager")
    public String getCertManagerMetrics() {
        return service.getCertManagerMetrics();
    }
    @ResponseBody
    @GetMapping("/metrics/oss/argocd")
    public String getArgocdMetrics() {
        return service.getArgocdMetrics();
    }
    @ResponseBody
    @GetMapping("/metrics/oss/tekton")
    public String getTektonMetrics() {
        return service.getTektonMetrics();
    }
    @ResponseBody
    @GetMapping("/metrics/oss/harbor")
    public String getHarborMetrics() {
        return service.getHarborMetrics();

    }
    @ResponseBody
    @GetMapping("/metrics/oss/loki")
    public String getLokiMetrics() {
        return service.getLokiMetrics();
    }
    @ResponseBody
    @GetMapping("/metrics/oss/custom")
    public String getCustomMetrics() {
        return service.getCustomMetrics();
    }
    @ResponseBody
    @GetMapping("/metrics/oss/oss")
    public String getOssMetrics() {
        return service.getOssMetrics();
    }

    // kubernetes 메트릭스
    @ResponseBody
    @GetMapping("/metrics/kubernetes")
    public String getMetrics() {
        return service.getMetrics();
    }
    @ResponseBody
    @GetMapping("/metrics/kubernetes/node/proxy")
    public String getNodeProxyMetrics() {
        return service.getNodeProxyMetrics();
    }
    @ResponseBody
    @GetMapping("/metrics/kubernetes/node/cadvisor")
    public String getNodeCadvisorMetrics() {
        return service.getNodeCadvisorMetrics();
    }
}
