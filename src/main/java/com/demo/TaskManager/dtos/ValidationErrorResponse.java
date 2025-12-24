package com.demo.TaskManager.dtos;


import java.util.List;

public record ValidationErrorResponse(
        int status,
        String message,
        List<Violation> errors
) {
    public record Violation(String field, String message) {}
}
