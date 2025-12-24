package com.demo.TaskManager.dtos;

import lombok.Builder;

@Builder
public record UserInfoResponse(
        Long id,
        String email,
        String name
) {
}

