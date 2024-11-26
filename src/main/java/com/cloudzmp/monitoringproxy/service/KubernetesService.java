package com.cloudzmp.monitoringproxy.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cloudzmp.monitoringproxy.component.KubernetesManager;

import io.fabric8.kubernetes.api.model.NamespaceList;
import io.fabric8.kubernetes.api.model.NodeList;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.kubernetes.client.Client;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KubernetesService {

    private final KubernetesManager km;

    public Client getClient() {
        return km.getClient();
    }

    public List<String> getServiceList() {
        ServiceList svcList = km.getClient().services().list();
        return svcList.getItems().parallelStream()
                .map(svc -> svc.getMetadata().getName())
                .toList();
    }

    public List<String> getNamespaceList() {
        NamespaceList nsList = km.getClient().namespaces().list();
        return nsList.getItems().parallelStream()
                .map(ns -> ns.getMetadata().getName())
                .toList();
    }

    public List<String> getNodeList() {
        NodeList nodelist = km.getClient().nodes().list();
        return nodelist.getItems().parallelStream()
                .map(node -> node.getMetadata().getName())
                .toList();
    }

    public List<String> getPodList() {
        PodList podList = km.getClient().pods().list();
        return podList.getItems().parallelStream()
                .map(pod -> pod.getMetadata().getName())
                .toList();
    }
}
