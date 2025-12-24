package com.demo.TaskManager.services;

import com.demo.TaskManager.common.exceptions.ResourceNotFoundException;
import com.demo.TaskManager.dtos.ProjectProgressResponse;
import com.demo.TaskManager.dtos.ProjectRequest;
import com.demo.TaskManager.dtos.ProjectResponse;
import com.demo.TaskManager.entities.Project;
import com.demo.TaskManager.entities.User;
import com.demo.TaskManager.repositories.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Transactional
    public ProjectResponse createProject(ProjectRequest request, User user) {
        Project project = Project.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .user(user)
                .build();

        Project savedProject = projectRepository.save(project);
        return mapToResponse(savedProject);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getUserProjects(Long userId) {
        List<Project> projects = projectRepository.findByUserId(userId);
        return projects.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(Long projectId, Long userId) {
        Project project = projectRepository.findByIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        return mapToResponse(project);
    }

    @Transactional
    public ProjectResponse updateProject(Long projectId, ProjectRequest request, Long userId) {
        Project project = projectRepository.findByIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        project.setTitle(request.getTitle());
        project.setDescription(request.getDescription());

        Project updatedProject = projectRepository.save(project);
        return mapToResponse(updatedProject);
    }

    @Transactional
    public void deleteProject(Long projectId, Long userId) {
        Project project = projectRepository.findByIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        projectRepository.delete(project);
    }

    @Transactional(readOnly = true)
    public ProjectProgressResponse getProjectProgress(Long projectId, Long userId) {
        Project project = projectRepository.findByIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        return ProjectProgressResponse.builder()
                .projectId(project.getId())
                .projectTitle(project.getTitle())
                .totalTasks(project.getTotalTasks())
                .completedTasks(project.getCompletedTasks())
                .progressPercentage(project.getProgressPercentage())
                .build();
    }

    private ProjectResponse mapToResponse(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .totalTasks(project.getTotalTasks())
                .completedTasks(project.getCompletedTasks())
                .progressPercentage(project.getProgressPercentage())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }
}

