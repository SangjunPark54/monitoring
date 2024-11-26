package com.cloudzmp.monitoringproxy.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "zcp")
public class MetricProperties {
    @Data
    @Component
    public static class Endpoints {
        private List<String> k8s;
        private List<String> nginx;
        private List<String> es;
        private List<String> cert;
        private List<String> argocd;
        private List<String> tekton;
        private List<String> harbor;
        private List<String> loki;
        private List<String> Oss;
    }
    @Data
    @Component
    public static class GrantMetrics {
        private List<String> ingress;
        private List<String> es;
        private List<String> cert;
        private List<String> tekton;
        private List<String> kubeMetric;
        private List<String> kubeNode;
        private List<String> mysql;
        private List<String> cortex;
        private List<String> loki;
        private List<String> mongo;
        private List<String> istio;
        private List<String> node;
        private List<String> harbor;
        private List<String> argocd;
        private List<String> postgresql;
        private List<String> kubeStateMetrics;
        private List<String> oss;
    }
}
