package com.cloudzmp.metricagent.component;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@Configuration
@RequiredArgsConstructor
public class KubernetesManager {

    private KubernetesClient client;

    @Bean
    public KubernetesClient getClient() {
        try {
            log.debug("call prd kubernetes");
            client = new KubernetesClientBuilder().build();
        } catch (Exception e) {
            log.error("Failed to initialize Kubernetes client", e);
            throw new RuntimeException("Kubernetes client initialization failed", e);
        }
        return client;
    }

//    @Value("${zcp.token}")
//    private String token;

//    @Bean
//    public KubernetesClient kubernetesClient() {
//        if ("dev".equals(activeProfile)) {
//            return devInit();
//        }
//        return prdInit();
//    }

//    public KubernetesClient devInit() {
//        log.debug("call dev kubernetes");
//        try {
//            Config config = new ConfigBuilder()
//                    .withMasterUrl("https://kubernetes.docker.internal:6443") // 클러스터 내부 API 서버 URL
//                    .withNamespace("default") // 네임스페이스 경로
//                    .withOauthToken(token)
//                    .withTrustCerts(true)
//                    .build();
//
//            client = new KubernetesClientBuilder()
//                    .withConfig(config)
//                    .build();
//        } catch (Exception e) {
//            throw new RuntimeException("Kubernetes client initialization failed", e);
//        }
//        return client;
//    }
}
