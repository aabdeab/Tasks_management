package com.demo.TaskManager.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;


@Builder
public record ErrorResponse(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
        LocalDateTime timestamp,
        int status,
        String error,
        String message
) {

    public ErrorResponse(HttpStatus status, String message) {
        this(LocalDateTime.now(), status.value(), status.getReasonPhrase(), message);
    }
    public ErrorResponse(String error, int status, String message) {
        this(LocalDateTime.now(), status, error, message);
    }
}