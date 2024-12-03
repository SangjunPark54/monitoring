package com.cloudzmp.metricagent.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class MetricCollectionException extends RuntimeException {
    public MetricCollectionException(String message) {
        super(message);
    }

    public MetricCollectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
