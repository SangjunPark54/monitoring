package com.cloudzmp.monitoringproxy.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@RequiredArgsConstructor
@Configuration
public class KubernetesManager {

    private KubernetesClient client;
    @Value("${zcp.token}")
    private String token;

    @PostConstruct
    public void devInit() {
        try {
            Config config = new ConfigBuilder()
                    .withMasterUrl("https://kubernetes.docker.internal:6443") // 클러스터 내부 API 서버 URL
                    .withNamespace("default") // 네임스페이스 경로
                    .withOauthToken(token)
                    .withTrustCerts(true)
                    .build();

            client = new KubernetesClientBuilder()
                    .withConfig(config)
                    .build();
            log.info("K8s client initialized");
        } catch (Exception e) {
            log.error("Failed to initialize Kubernetes client", e);
            throw new RuntimeException("Kubernetes client initialization failed", e);
        }
    }
//    @PostConstruct
//    public void init2() {
//        try {
//            // KubernetesClientBuilder를 사용하여 클라이언트 생성
//            client = new KubernetesClientBuilder().build();
//            log.info("K8s client initialized using Service Account: zcp-mcm-backend-service-admin");
//        } catch (Exception e) {
//            log.error("Failed to initialize Kubernetes client", e);
//            throw new RuntimeException("Kubernetes client initialization failed", e);
//        }
//}


}
