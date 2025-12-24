package com.demo.TaskManager.mappers;

import com.demo.TaskManager.dtos.ProjectProgressResponse;
import com.demo.TaskManager.dtos.ProjectRequest;
import com.demo.TaskManager.dtos.ProjectResponse;
import com.demo.TaskManager.entities.Project;
import com.demo.TaskManager.entities.User;

/**
 * Mapper utility class for Project entity transformations
 */
public class ProjectMapper {

    private ProjectMapper() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    /**
     * Convertit un ProjectRequest DTO en entité Project
     * @param request le ProjectRequest à convertir
     * @param user l'utilisateur propriétaire du projet
     * @return Project entity
     */
    public static Project fromRequest(ProjectRequest request, User user) {
        if (request == null) {
            return null;
        }

        return Project.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .user(user)
                .build();
    }

    /**
     * Convertit une entité Project en ProjectResponse DTO
     * @param project l'entité Project à convertir
     * @return ProjectResponse DTO
     */
    public static ProjectResponse toResponse(Project project) {
        if (project == null) {
            return null;
        }

        return ProjectResponse.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .totalTasks(project.getTotalTasks())
                .completedTasks(project.getCompletedTasks())
                .progressPercentage(project.getProgressPercentage())
                .createdAt(project.getCreatedAt())
                .build();
    }

    /**
     * Convertit une entité Project en ProjectProgressResponse DTO
     * @param project l'entité Project à convertir
     * @return ProjectProgressResponse DTO
     */
    public static ProjectProgressResponse toProgressResponse(Project project) {
        if (project == null) {
            return null;
        }

        return ProjectProgressResponse.builder()
                .projectId(project.getId())
                .projectTitle(project.getTitle())
                .totalTasks(project.getTotalTasks())
                .completedTasks(project.getCompletedTasks())
                .progressPercentage(project.getProgressPercentage())
                .build();
    }
}

