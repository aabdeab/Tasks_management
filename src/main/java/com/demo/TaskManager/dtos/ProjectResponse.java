package com.demo.TaskManager.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectResponse {
    private Long id;
    private String title;
    private String description;
    private int totalTasks;
    private long completedTasks;
    private double progressPercentage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

