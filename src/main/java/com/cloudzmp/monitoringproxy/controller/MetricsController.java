package com.cloudzmp.monitoringproxy.controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cloudzmp.monitoringproxy.service.MetricHandler;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class MetricsController {

    private final MetricHandler metricHandler;

    public MetricsController(MetricHandler metricHandler) {
        this.metricHandler = metricHandler;
    }
    @GetMapping("/metrics")
    @ResponseBody
    public List<String> getFilteredMetrics() {
        return metricHandler.collectMetricsList();
    }

    @GetMapping("/getKubeStateMetrics")
    @ResponseBody
    public String getKubeStateMetrics() {
        return metricHandler.getKubeStateMetrics();
    }

    @GetMapping("/getOssMetricsFromEndpoint")
    @ResponseBody
    public String getOssMetricsFromEndpoint() {
        return metricHandler.getOssMetricsFromEndpoint();
    }

    @GetMapping("/getOssMetricsFromEndpoint2")
    @ResponseBody
    public CompletableFuture<ResponseEntity<String>> getOssMetricsFromEndpoint2() {
        return CompletableFuture.supplyAsync(() -> {
            String result = metricHandler.getOssMetricsFromEndpoint();
            return ResponseEntity.ok(result);
        });
    }

    /*
    apiserver_request_duration_seconds_bucket
    apiserver_request_duration_seconds_sum
    apiserver_request_duration_seconds_count
    apiserver_request_total
    go_goroutines
    process_cpu_seconds_total
    process_open_fds
    process_resident_memory_bytes
    rest_client_request_duration_seconds_bucket
    rest_client_requests_total
    workqueue_adds_total
    workqueue_depth
    workqueue_queue_duration_seconds_bucket
     */

    @GetMapping("/getMetrics")
    @ResponseBody
    public String getMetrics() {
        return metricHandler.getMetrics();
    }

    /*
    go_goroutines
    kubelet_cgroup_manager_duration_seconds_bucket
    kubelet_cgroup_manager_duration_seconds_count
    kubelet_pleg_relist_duration_seconds_bucket
    kubelet_pleg_relist_duration_seconds_count
    kubelet_pleg_relist_interval_seconds_bucket
    kubelet_pod_start_duration_seconds_sum
    kubelet_pod_start_duration_seconds_count
    kubelet_pod_worker_duration_seconds_bucket
    kubelet_pod_worker_duration_seconds_count
    kubelet_running_containers
    kubelet_running_pods 24
    kubelet_runtime_operations_duration_seconds_bucket
    kubelet_runtime_operations_errors_total
    kubelet_runtime_operations_total
    process_cpu_seconds_total
    process_open_fds
    process_resident_memory_bytes
    process_start_time_seconds
    rest_client_request_duration_seconds_bucket
    rest_client_requests_total
    storage_operation_duration_seconds_bucket
    storage_operation_duration_seconds_count
    volume_manager_total_volumes
    workqueue_adds_total
    workqueue_depth
    workqueue_queue_duration_seconds_bucket
     */
    @GetMapping("/getKubeNodeMetrics")
    @ResponseBody
    public String getKubeNodeMetrics() {
        return metricHandler.getKubeNodeMetrics();
    }
    /*
    container_cpu_usage_seconds_total
    container_fs_limit_bytes
    container_fs_reads_bytes_total
    container_fs_usage_bytes
    container_fs_writes_bytes_total
    container_memory_rss
    container_memory_usage_bytes
    container_memory_working_set_bytes
    container_network_receive_bytes_total
    container_network_receive_errors_total
     */
    @GetMapping("getNodenameProxyMetrics")
    @ResponseBody
    public String getNodeMetrics() {
        return metricHandler.getNodenameProxyMetrics();
    }

}
