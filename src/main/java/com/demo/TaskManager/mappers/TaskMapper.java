package com.demo.TaskManager.mappers;

import com.demo.TaskManager.dtos.TaskRequest;
import com.demo.TaskManager.dtos.TaskResponse;
import com.demo.TaskManager.entities.Project;
import com.demo.TaskManager.entities.Task;

/**
 * Mapper utility class for Task entity transformations
 */
public class TaskMapper {

    private TaskMapper() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    /**
     * Convertit un TaskRequest DTO en entité Task
     * @param request le TaskRequest à convertir
     * @param project le projet auquel la tâche appartient
     * @return Task entity
     */
    public static Task fromRequest(TaskRequest request, Project project) {
        if (request == null) {
            return null;
        }

        return Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .dueDate(request.getDueDate())
                .completed(false)
                .project(project)
                .build();
    }

    /**
     * Convertit une entité Task en TaskResponse DTO
     * @param task l'entité Task à convertir
     * @return TaskResponse DTO
     */
    public static TaskResponse toResponse(Task task) {
        if (task == null) {
            return null;
        }

        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .dueDate(task.getDueDate())
                .completed(task.isCompleted())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}

