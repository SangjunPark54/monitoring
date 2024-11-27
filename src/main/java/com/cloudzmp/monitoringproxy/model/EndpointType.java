package com.cloudzmp.monitoringproxy.model;

import java.util.List;

public enum EndpointType {
    k8s,
    nginx,
    es,
    cert,
    argocd,
    tekton,
    harbor,
    loki,
    oss,
    custom
}
