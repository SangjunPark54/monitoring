package com.cloudzmp.metricagent.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.cloudzmp.metricagent.config.MetricProperties;
import com.cloudzmp.metricagent.config.RestTemplateConfig;
import com.cloudzmp.metricagent.exception.MetricCollectionException;
import com.cloudzmp.metricagent.logging.LogMetricCollection;
import com.cloudzmp.metricagent.model.EndpointType;
import com.cloudzmp.metricagent.model.GrantMetricType;

import io.fabric8.kubernetes.client.dsl.internal.OperationSupport;
import lombok.extern.slf4j.Slf4j;
import static java.util.concurrent.Executors.newFixedThreadPool;

@Service
@Slf4j
public class MetricCollector {

    private final RestTemplate restTemplate;
    private final KubernetesService ks;
    private final MetricProperties mp;

    public MetricCollector(MetricProperties mp, KubernetesService ks) throws Exception {
        this.mp = mp;
        this.ks = ks;
        this.restTemplate = new RestTemplateConfig().createRestTemplate();
    }

    @LogMetricCollection
    public String getNginxIngressMetrics() {
        List<String> endpoints = getEndpointUrl(EndpointType.nginx);
        List<String> gMetrics = getGrantMetric(GrantMetricType.nginx);

        return getGrantMetricFromListOfRawData(endpoints, gMetrics);
    }

    @LogMetricCollection
    public String getElasticSearchMetrics() {
        List<String> endpoints = getEndpointUrl(EndpointType.es);
        List<String> gMetrics = getGrantMetric(GrantMetricType.es);;

        return getGrantMetricFromListOfRawData(endpoints, gMetrics);
    }

    @LogMetricCollection
    public String getCertManagerMetrics() {
        List<String> endpoints = getEndpointUrl(EndpointType.cert);
        List<String> gMetrics = getGrantMetric(GrantMetricType.cert);

        return getGrantMetricFromListOfRawData(endpoints, gMetrics);
    }

    @LogMetricCollection
    public String getArgocdMetrics() {
        List<String> endpoints = getEndpointUrl(EndpointType.argocd);
        List<String> gMetrics = getGrantMetric(GrantMetricType.argocd);

        return getGrantMetricFromListOfRawData(endpoints, gMetrics);
    }

    @LogMetricCollection
    public String getTektonMetrics() {
        List<String> endpoints = getEndpointUrl(EndpointType.tekton);
        List<String> gMetrics = getGrantMetric(GrantMetricType.tekton);

        return getGrantMetricFromListOfRawData(endpoints, gMetrics);
    }

    @LogMetricCollection
    public String getHarborMetrics() {
        List<String> endpoints = getEndpointUrl(EndpointType.harbor);
        List<String> gMetrics = getGrantMetric(GrantMetricType.harbor);

        return getGrantMetricFromListOfRawData(endpoints, gMetrics);

    }

    @LogMetricCollection
    public String getLokiMetrics() {
        List<String> endpoints = getEndpointUrl(EndpointType.loki);
        List<String> gMetrics = getGrantMetric(GrantMetricType.loki);

        return getGrantMetricFromListOfRawData(endpoints, gMetrics);
    }

    @LogMetricCollection
    public String getCustomMetrics() {
        List<String> endpoints = getEndpointUrl(EndpointType.custom);
        List<String> gMetrics = getGrantMetric(GrantMetricType.custom);

        return getGrantMetricFromListOfRawData(endpoints, gMetrics);
    }

    @LogMetricCollection
    public String getOssMetrics() {
        List<String> endpoints = getEndpointUrl(EndpointType.oss);
        StringBuilder sb = new StringBuilder();
        for (String endpoint : endpoints) {
            sb.append(collectRawMetric(endpoint));
        }
        return sb.toString();
    }

    @LogMetricCollection
    public String getMetrics() {
        String endpoint = getEndpointUrl(EndpointType.k8s).getFirst();
        List<String> gMetrics = getGrantMetric(GrantMetricType.kubeMetric);

        String response = getOsResponse(endpoint, null,null,"/metrics");

        return getGrantMetricsFromRawData(response, gMetrics);
    }
    @LogMetricCollection
    public String getNodeProxyMetrics() {
        String result = "";
        String endpoint = getEndpointUrl(EndpointType.k8s).getFirst();
        List<String> gMetrics = getGrantMetric(GrantMetricType.kubeNodeMetric);

        try {
            List<String> nodeList = ks.getNodeList();
            for (String node : nodeList) {
                String response = getOsResponse(endpoint, "/api/v1/nodes/", node, "/proxy/metrics");
                return getGrantMetricsFromRawData(response, gMetrics);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch metrics for node ", e);
        }
        return result;
    }

    @LogMetricCollection
    public String getNodeCadvisorMetrics() {
        String result = "";
        String endpoint = getEndpointUrl(EndpointType.k8s).getFirst();
        List<String> gMetrics = getGrantMetric(GrantMetricType.kubeCadvisor);

        List<String> nodeList = ks.getNodeList();
        for (String node : nodeList) {
            String response = getOsResponse(endpoint, "/api/v1/nodes/",node,"/proxy/metrics/cadvisor");
            return getGrantMetricsFromRawData(response, gMetrics);
        }
        return result;
    }

    private String getOsResponse(String endpoint, String firstPath, String resource, String lastPath) {
        OperationSupport os = new OperationSupport(ks.getClient());
        String requestURI = StringUtils.join(endpoint, firstPath, resource, lastPath);
        return os.handleRaw(String.class, requestURI, "GET", null);
    }

    private String getGrantMetricFromListOfRawData(List<String> endpointList, List<String> grantMetrics) {
        StringBuilder result = new StringBuilder();
        ExecutorService executor = newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<String>> futures = new ArrayList<>();

        try {
            for (String endpoint : endpointList) {
                log.info("Start to collect Grant Metrics From endpoint : {}", endpoint);
                futures.add(executor.submit(() -> {
                    String response = collectRawMetric(endpoint);
                    return getGrantMetricsFromRawData(response, grantMetrics);
                }));
            }
            for (Future<String> future : futures) {
                result.append(future.get()).append("\n");
            }
        } catch (Exception e) {
            log.error("Failed to get grant metrics from: {}",endpointList, e);
            throw new RuntimeException("Failed to get grant metrics : ",e);
        } finally {
            executor.shutdown();
        }
        return result.toString();
    }

    private String collectRawMetric(String svcName) {
        try {
            String rawMetric = restTemplate.getForObject(svcName, String.class);
            log.info("Finished collecting raw metric from service : {}", svcName);
            return rawMetric;
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to collect metric from service : ", e);
        }
    }

    private String getGrantMetricsFromRawData(String rawMetrics, List<String> grantMetrics) {
        List<String> lines = List.of(rawMetrics.split("\n"));
        int numThreads = Runtime.getRuntime().availableProcessors();

        try (ExecutorService executor = newFixedThreadPool(numThreads)) {
            try {
                List<Callable<List<String>>> tasks = lines.stream()
                        .collect(Collectors.groupingBy(line -> lines.indexOf(line) % numThreads))
                        .values().stream()
                        .map(chunk -> (Callable<List<String>>) () ->
                                chunk.stream()
                                        .filter(line -> grantMetrics.stream().anyMatch(line::contains))
                                        .collect(Collectors.toList()))
                        .collect(Collectors.toList());

                List<Future<List<String>>> results = executor.invokeAll(tasks);

                return results.stream()
                        .flatMap(future -> {
                            try {
                                return future.get().stream();
                            } catch (InterruptedException | ExecutionException e) {
                                throw new RuntimeException("Error while processing grant metrics from collected metrics", e);
                            }
                        })
                        .collect(Collectors.joining("\n"));
            } catch (InterruptedException e) {
                throw new RuntimeException("Error while processing grant metrics from collected metrics", e);
            } finally {
                executor.shutdown();
            }
        }
    }

    private List<String> getEndpointUrl(EndpointType type) {
        List<String> endpoints = switch (type) {
            case k8s -> mp.getEndpoints().getK8s();
            case nginx -> mp.getEndpoints().getNginx();
            case es -> mp.getEndpoints().getEs();
            case cert -> mp.getEndpoints().getCert();
            case argocd -> mp.getEndpoints().getArgocd();
            case tekton -> mp.getEndpoints().getTekton();
            case harbor -> mp.getEndpoints().getHarbor();
            case loki -> mp.getEndpoints().getLoki();
            case oss -> mp.getEndpoints().getOss();
            case custom -> mp.getEndpoints().getCustom();
            default -> throw new MetricCollectionException("Endpoint type not supported: " + type);
        };

        if (endpoints == null || endpoints.isEmpty()) {
            throw new MetricCollectionException(type + "Endpoint type is empty");
        }

        return endpoints;
    }

    private List<String> getGrantMetric(GrantMetricType type) {
        List<String> grantMetric = switch (type) {
            case nginx -> mp.getGrantMetric().getNginx();
            case es -> mp.getGrantMetric().getEs();
            case cert -> mp.getGrantMetric().getCert();
            case tekton -> mp.getGrantMetric().getTekton();
            case mysql -> mp.getGrantMetric().getMysql();
            case cortex -> mp.getGrantMetric().getCortex();
            case loki -> mp.getGrantMetric().getLoki();
            case mongo -> mp.getGrantMetric().getMongo();
            case istio -> mp.getGrantMetric().getIstio();
            case harbor -> mp.getGrantMetric().getHarbor();
            case argocd -> mp.getGrantMetric().getArgocd();
            case postgresql -> mp.getGrantMetric().getPostgresql();
            case oss -> mp.getGrantMetric().getOss();
            case custom -> mp.getGrantMetric().getCustom();
            case kubeCadvisor -> mp.getGrantMetric().getKubeCadvisor();
            case kubeMetric -> mp.getGrantMetric().getKubeMetric();
            case kubeNodeMetric -> mp.getGrantMetric().getKubeNodeMetric();
            case kubeStateMetrics -> mp.getGrantMetric().getKubeStateMetrics();
            default -> throw new MetricCollectionException("Grant Metric is not supported: " + type);
        };

        if (grantMetric == null || grantMetric.isEmpty()) {
            throw new MetricCollectionException(type + "GrantMetric is not specified in request");
        }
        return grantMetric;
    }
    //    public String getNodeResourceMetrics() {
//        String result = "";
//        String endpoint = mp.getEndpoints().getK8s().get(0);
//        List<String> gMetrics = mp.getGrantMetric().getKubeMetric();
//        List<String> nodeList = ks.getNodeList();
//        for (String node : nodeList) {
//            String response = getOsResponse(endpoint,"GET","/api/v1/nodes/",node,"/proxy/metrics/resource");
//            return getAllowedMetrics(response, gMetrics);
//        }
//        return result;
//    }
}
