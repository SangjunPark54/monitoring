package com.cloudzmp.monitoringproxy.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.cloudzmp.monitoringproxy.config.MetricProperties;
import com.cloudzmp.monitoringproxy.config.RestTemplateConfig;

import io.fabric8.kubernetes.client.dsl.internal.OperationSupport;
import lombok.extern.slf4j.Slf4j;
import static java.util.concurrent.Executors.newFixedThreadPool;

@Service
@Slf4j
public class MetricCollector {

    private final RestTemplate restTemplate;
    private final KubernetesService ks;
    private final List<String> nginxEndpoint;
    private final List<String> esEndpoint;
    private final List<String> certEndpoint;
    private final List<String> argocdEndpoint;
    private final List<String> tektonEndpoint;
    private final List<String> harborEndpoint;
    private final List<String> lokiEndpoint;
    private final List<String> ossEndpoint;
    private final List<String> nginxMetrics;
    private final List<String> esMetrics;
    private final List<String> certMetrics;
    private final List<String> argocdMetrics;
    private final List<String> tektonMetrics;
    private final List<String> harborMetrics;
    private final List<String> lokiMetrics;
    private final List<String> ossMetrics;

    public MetricCollector(MetricProperties metricProperties, MetricProperties.Endpoints endpoints, MetricProperties.GrantMetrics grantMetrics, KubernetesService ks) throws Exception {
        this.nginxEndpoint = endpoints.getNginx();
        this.esEndpoint = endpoints.getEs();
        this.certEndpoint = endpoints.getCert();
        this.argocdEndpoint = endpoints.getArgocd();
        this.tektonEndpoint = endpoints.getTekton();
        this.harborEndpoint = endpoints.getHarbor();
        this.lokiEndpoint = endpoints.getLoki();
        this.ossEndpoint = endpoints.getOss();
        this.ks = ks;
        this.nginxMetrics = grantMetrics.getIngress();
        this.esMetrics = grantMetrics.getEs();
        this.certMetrics = grantMetrics.getCert();
        this.argocdMetrics = grantMetrics.getArgocd();
        this.tektonMetrics = grantMetrics.getTekton();
        this.harborMetrics = grantMetrics.getHarbor();
        this.lokiMetrics = grantMetrics.getLoki();
        this.ossMetrics = grantMetrics.getOss();

        this.restTemplate = new RestTemplateConfig().createRestTemplate();
    }

    public List<String> getAllMetrics() {
        log.info("Start getAllMetrics");
        List<String> result = ossEndpoint.parallelStream()
                .map(this::collectRawMetric)
//                .map(this::getAllowedMetrics)
                .toList();
        log.info("Done getAllMetrics");
        return result;
    }

    // 10초
//    public String getOssMetricsFromEndpoint() {
//        StringBuilder result = new StringBuilder();
//        try {
//            ossEndpoint.parallelStream() // 병렬 스트림 사용
//                    .map(this::collectRawMetric) // 각 엔드포인트에서 메트릭 수집
//                    .map(this::getAllowedMetrics) // 필터링
//                    .forEach(filtered -> result.append(filtered).append("\n")); // 결과 누적
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to fetch metrics for oss", e);
//        }
//        return result.toString();
//    }
    /*
    kube-state-metrics : deploy에서 처리
    node-exportor : ds에서 처리

        */
    public String getNginxIngressMetrics() {
        return getGrantMetricsFromRawData(nginxEndpoint, nginxMetrics);
    }

    public String getElasticSearchMetrics() {
        return getGrantMetricsFromRawData(esEndpoint, esMetrics);
    }

    public String getCertManagerMetrics() {
        return getGrantMetricsFromRawData(certEndpoint, certMetrics);
    }

    public String getArgocdMetrics() {
        return getGrantMetricsFromRawData(argocdEndpoint, argocdMetrics);
    }

    public String getTektonMetrics() {
        return getGrantMetricsFromRawData(tektonEndpoint, tektonMetrics);
    }

    public String getHarborMetrics() {
        return getGrantMetricsFromRawData(harborEndpoint, harborMetrics);
    }

    public String getLokiMetrics() {
        return getGrantMetricsFromRawData(lokiEndpoint, lokiMetrics);
    }

    public String getOssMetrics() {
        return getGrantMetricsFromRawData(ossEndpoint, ossMetrics);
    }

    private String getGrantMetricsFromRawData(List<String> endpointList, List<String> grantMetrics) {
        StringBuilder result = new StringBuilder();
        ExecutorService executor = newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<String>> futures = new ArrayList<>();

        try {
            for (String endpoint : endpointList) {
                log.info("Start to collect Grant Metrics From endpoint : {}", endpoint);
                futures.add(executor.submit(() -> {
                    String response = collectRawMetric(endpoint);
                    return getAllowedMetrics(response, grantMetrics);
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

//    public String getOssMetricsFromEndpoint() {
//        StringBuilder result = new StringBuilder();
//        ExecutorService executor = newFixedThreadPool(Runtime.getRuntime().availableProcessors());
//        List<Future<String>> futures = new ArrayList<>();
//
//        try {
//            for (String endpoint : ossEndpoint) {
//                futures.add(executor.submit(() -> {
//                    String response = collectRawMetric(endpoint);
//                    return getAllowedMetrics(response, ossMetrics);
//                }));
//            }
//
//            for (Future<String> future : futures) {
//                result.append(future.get()).append("\n"); // Future 결과 가져오기
//            }
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to fetch metrics for oss", e);
//        } finally {
//            executor.shutdown(); // ExecutorService 종료
//        }
//
//        return result.toString();
//    }

    private String collectRawMetric(String svcName) {
        try {
            String result = restTemplate.getForObject(svcName, String.class);
            log.info("Finished collecting raw metric from service : {}", svcName);
            return result;
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to collect metric from service : ", e);
        }
    }

    public String getAllowedMetrics(String rawMetrics, List<String> allowedMetrics) {
        List<String> lines = List.of(rawMetrics.split("\n"));
        int numThreads = Runtime.getRuntime().availableProcessors();

        try (ExecutorService executor = newFixedThreadPool(numThreads)) {
            try {
                List<Callable<List<String>>> tasks = lines.stream()
                        .collect(Collectors.groupingBy(line -> lines.indexOf(line) % numThreads))
                        .values().stream()
                        .map(chunk -> (Callable<List<String>>) () ->
                                chunk.stream()
                                        .filter(line -> allowedMetrics.stream().anyMatch(line::contains))
                                        .collect(Collectors.toList()))
                        .collect(Collectors.toList());

                List<Future<List<String>>> results = executor.invokeAll(tasks);

                return results.stream()
                        .flatMap(future -> {
                            try {
                                return future.get().stream();
                            } catch (InterruptedException | ExecutionException e) {
                                throw new RuntimeException("Error while processing metrics", e);
                            }
                        })
                        .collect(Collectors.joining("\n"));
            } catch (InterruptedException e) {
                throw new RuntimeException("Error while processing metrics", e);
            } finally {
                executor.shutdown();
            }
        }
    }

//    private String getAllowedMetrics(String rawMetrics) {
//        log.info("start getAllowedMetrics (split + fileter)");
//        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
//            return executor.submit(() ->
//                    Arrays.stream(rawMetrics.split("\n"))
//                            .parallel()
//                            .filter(raw -> allowedMetrics.stream().anyMatch(raw::contains))
//                            .collect(Collectors.joining("\n"))
//            ).get();
//        } catch (ExecutionException e) {
//            throw new RuntimeException("Error while processing metrics", e);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//    }

    // Is Not Metric
//    public String getNodeMetrics() {
//        String response = getOsResponse("GET","/api/v1/",null,"nodes");
//        return getAllowedMetrics(response);
//    }

    public String getIngressMetrics() {
        String response = getOsResponse("GET","/apis/",null,"networking.k8s.io/v1/ingresses");
        String response2 = getOsResponse("GET","/apis/",null,"extentions/v1beta1/ingresses");
        String result = StringUtils.join(response,response2);
        return getAllowedMetrics(result, ossMetrics);
    }

    public String getNodeProxyMetrics() {
        String result = "";
        try {
            List<String> nodeList = ks.getNodeList();
            for (String node : nodeList) {
                String response = getOsResponse("GET","/api/v1/nodes/", node, "/proxy/metrics");

                return getAllowedMetrics(response, ossMetrics);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch metrics for node ", e);
        }
        return result;
    }

    public String getMetrics() {
        String response = getOsResponse("GET",null,null,"/metrics");
        return getAllowedMetrics(response, ossMetrics);
    }

    public String getNodenameProxyMetrics() {
        String result = "";
        List<String> nodeList = ks.getNodeList();
        for (String node : nodeList) {
            String response = getOsResponse("GET","/api/v1/nodes/",node,"/proxy/metrics/cadvisor");
            return getAllowedMetrics(response, ossMetrics);
        }
        return result;
    }

    //NOT METRICS
    public String getServiceMetrics() {
        try {
            String response = getOsResponse("GET","/api/v1/",null,"services");
            return getAllowedMetrics(response, ossMetrics);
        } catch (Exception e) {
            throw new RuntimeException("Error get metrics from service {}", e);
        }
    }

    // NOT METRICS
    public String getServiceNamespaceMetrics() {
        String result = "";
        try {
            List<String> nsList = ks.getNamespaceList();
            for (String ns : nsList) {
                String response = getOsResponse("GET","/api/v1/namespaces/", ns, "/services");
                return getAllowedMetrics(response, ossMetrics);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error get metrics from services metrics", e);
        }
        return result;
    }

    public String getPodMetrics() {
        try {
            String response = getOsResponse("GET","/api/v1/",null,"pods");
            return getAllowedMetrics(response, ossMetrics);
        } catch (Exception e) {
            throw new RuntimeException("Error get metrics from services metrics", e);
        }
    }

    public String getNamespacePodMetrics() {
        String result = "";
        try {
            List<String> nsList = ks.getNamespaceList();
            for (String ns : nsList) {
                String response = getOsResponse("GET","/api/v1/namespaces/",ns,"/pods");
                return getAllowedMetrics(response, ossMetrics);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error get metrics from services metrics", e);
        }
        return result;
    }

    public String getEndpointsMetrics() {
        try {
            String response = getOsResponse("GET","/api/v1/",null,"endpoints");
            return getAllowedMetrics(response, ossMetrics);
        } catch (Exception e) {
            throw new RuntimeException("Error while processing metrics for endpoint", e);
        }
    }

    public String getNamespaceEndpointMetrics() {
        String result = "";
        try {
            List<String> namespaceList = ks.getNamespaceList();
            for (String namespace : namespaceList) {
                String response = getOsResponse("GET","/api/v1/namespaces/", namespace, "/endpoints");
                return getAllowedMetrics(response, ossMetrics);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch metrics for endpoint ", e);
        }
        return result;
    }

    private String getOsResponse(String method, String firstPath, String resource, String lastPath) {
        OperationSupport os = new OperationSupport(ks.getClient());
        String endpoint = StringUtils.join(kubeEndpoint, firstPath, resource, lastPath);
        return os.handleRaw(String.class, endpoint, method, null);
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

//        List<String> nodeList = getNodeList();
//        String bearerToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6IlJCTE1lT3NUUWFpZVhmRzVKazRWOThnblZ1T3ExdE1YWUpJTkZzM1RPREEifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJ6Y3Atc3lzdGVtIiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZWNyZXQubmFtZSI6InpjcC1tY20tYmFja2VuZC1zZXJ2aWNlLWFkbWluIiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQubmFtZSI6InpjcC1tY20tYmFja2VuZC1zZXJ2aWNlLWFkbWluIiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQudWlkIjoiMTkyZWQ1YzUtOTQ0YS00OGI2LTk3MWYtNTRjNTg5NGEyMGFlIiwic3ViIjoic3lzdGVtOnNlcnZpY2VhY2NvdW50OnpjcC1zeXN0ZW06emNwLW1jbS1iYWNrZW5kLXNlcnZpY2UtYWRtaW4ifQ.Q2BRCaDezoXzgUgGGBcfWrFgoaFO-th3GXxUmuZfJKwa_Gk03754IZmbX3yI1psXa6LfVv60CgHHraamKyFLuAjQfjlAEd55PnpaobSQHSjrA-2Vuz4SzC0QB_0yIThRdmejv8DdvF2x7RvWh0GADW56G6IuX5MEPdqZTHT8pjvROvhIHEz5w3z5TXsZB-ipKOmAnkCzX0zSg6Xj2y28o9N3XvimBambfa0ZIxm0jqI28RfIUXp7TozoIGqBbrTVZ5YCfqxNS2wYy2bd4RtBm3ksCy94JptwDBsLK4qe6OAAIeaSlwnt5MzbtxK61fuIZbXYaA7yPQwJ0EETP6tzUw";
//        for (String node : nodeList) {
//            String endpoint = StringUtils.join(kubeEndpoint+"/api/v1/nodes/"+node+"/proxy/metrics");
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("Authorization", "Bearer " + bearerToken);
//            HttpEntity<String> entity = new HttpEntity<>(headers);
//
//            ResponseEntity<String> response = restTemplate.exchange(
//                    endpoint, HttpMethod.GET, entity, String.class
//            );
//            result.put(node,getAllowedMetrics(response.getBody()));
//        }
//        return result;

//    public static List<String> convertJsonToList(String jsonArray) throws JsonProcessingException {
//        List<String> imageList = new ArrayList<>();
//
//        try {
//            // JSON 파싱을 위한 ObjectMapper 생성
//            ObjectMapper objectMapper = new ObjectMapper();
//
//            // JSON 데이터를 JsonNode로 변환
//            JsonNode rootNode = objectMapper.readTree(jsonArray);
//
//            // items 배열 추출
//            JsonNode itemsNode = rootNode.path("items");
//
//            // 각 노드의 이미지 정보 추출
//            for (JsonNode item : itemsNode) {
//                JsonNode imagesNode = item.path("status").path("images");
//
//                // 이미지 배열 순회
//                for (JsonNode image : imagesNode) {
//                    JsonNode namesNode = image.path("names");
//                    long sizeBytes = image.path("sizeBytes").asLong();
//
//                    // 각 이름에 대해 문자열 생성 및 리스트에 추가
//                    for (Iterator<JsonNode> it = namesNode.elements(); it.hasNext();) {
//                        String imageName = it.next().asText();
//                        imageList.add(imageName + " - " + sizeBytes);
//                    }
//                }
//            }
//        } catch (JsonProcessingException e) {
//            throw e;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return imageList;
//    }

