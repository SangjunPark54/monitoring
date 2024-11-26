package com.cloudzmp.monitoringproxy.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.cloudzmp.monitoringproxy.component.KubernetesManager;

import io.fabric8.kubernetes.api.model.metrics.v1beta1.NodeMetrics;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.NodeMetricsList;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class NodeService {

    private final KubernetesManager kubernetesManager;

    public List<String> test() {
        System.setProperty("kubernetes.trust.certificates", "true");

        try(KubernetesClient client = kubernetesManager.getClient()) {
            NodeMetricsList nMetricsList = client.top().nodes().metrics();

            List<String> result = new ArrayList<>();
            for(NodeMetrics nMetrics : nMetricsList.getItems()) {
                result.add(nMetrics.getMetadata().getName());
                result.add(nMetrics.getUsage().get("cpu").toString());
                result.add(nMetrics.getUsage().get("memory").toString());
                return result;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
