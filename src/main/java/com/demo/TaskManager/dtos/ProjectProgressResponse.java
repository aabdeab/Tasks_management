package com.demo.TaskManager.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectProgressResponse {
    private Long projectId;
    private String projectTitle;
    private int totalTasks;
    private long completedTasks;
    private double progressPercentage;
}

