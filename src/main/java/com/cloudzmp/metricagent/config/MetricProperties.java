package com.cloudzmp.metricagent.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;
import lombok.ToString;

@Data
@ConfigurationProperties(prefix = "zcp")
public class MetricProperties {
    private Endpoints endpoints;
    private GrantMetric grantMetric;

    @Data
    @ToString
    public static class Endpoints {
        private List<String> k8s;
        private List<String> nginx;
        private List<String> es;
        private List<String> cert;
        private List<String> argocd;
        private List<String> tekton;
        private List<String> harbor;
        private List<String> loki;
        private List<String> oss;
        private List<String> custom;
    }

    @Data
    @ToString
    public static class GrantMetric {
        private List<String> nginx;
        private List<String> es;
        private List<String> cert;
        private List<String> tekton;
        private List<String> mysql;
        private List<String> cortex;
        private List<String> loki;
        private List<String> mongo;
        private List<String> istio;
        private List<String> harbor;
        private List<String> argocd;
        private List<String> postgresql;
        private List<String> oss;
        private List<String> custom;

        private List<String> kubeCadvisor;
        private List<String> kubeMetric;
        private List<String> kubeNodeMetric;
        private List<String> kubeStateMetrics;
    }
}
