//package com.cloudzmp.metricagent.controller;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.cloudzmp.metricagent.service.NodeService;
//
//import lombok.RequiredArgsConstructor;
//
//@RestController
//@RequiredArgsConstructor
//public class KubernetesController {
//
//    private final NodeService nodeService;
//    @GetMapping("/getK8s")
//    public ResponseEntity<Object> getK8s() {
//        return ResponseEntity.ok(nodeService.test());
//    }
//
//}
