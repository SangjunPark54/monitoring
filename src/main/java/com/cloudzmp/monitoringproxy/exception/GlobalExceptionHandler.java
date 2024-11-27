package com.cloudzmp.monitoringproxy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.cloudzmp.monitoringproxy.model.ErrorResponse;

import io.fabric8.kubernetes.client.KubernetesClientException;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(KubernetesClientException.class)
    public ResponseEntity<Object> handleKubernetesClientException(KubernetesClientException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                "Failed to Call Kubernetes API Server",
                e.getMessage(),
                HttpStatus.SERVICE_UNAVAILABLE.value()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(MetricCollectionException.class)
    public ResponseEntity<ErrorResponse> handleMetricException(MetricCollectionException ex) {
        ErrorResponse error = new ErrorResponse(
                "Failed to collect metrics from endpoint",
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
