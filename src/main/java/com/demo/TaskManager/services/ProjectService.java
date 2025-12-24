package com.demo.TaskManager.services;

import com.demo.TaskManager.common.exceptions.ProjectNotFoundException;
import com.demo.TaskManager.common.exceptions.UserNotFoundException;
import com.demo.TaskManager.dtos.ProjectProgressResponse;
import com.demo.TaskManager.dtos.ProjectRequest;
import com.demo.TaskManager.dtos.ProjectResponse;
import com.demo.TaskManager.entities.Project;
import com.demo.TaskManager.entities.User;
import com.demo.TaskManager.mappers.ProjectMapper;
import com.demo.TaskManager.repositories.ProjectRepository;
import com.demo.TaskManager.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Transactional
    public ProjectResponse createProject(ProjectRequest request, Long userId) {
        log.info("[PROJECT] Creating new project '{}' for user with id: {}", request.getTitle(), userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("[PROJECT] User not found with id: {}", userId);
                    return new UserNotFoundException("User not found with id: " + userId);
                });

        Project project = ProjectMapper.fromRequest(request, user);

        Project savedProject = projectRepository.save(project);
        log.info("[PROJECT] Project created successfully with id: {} for user: {}",
            savedProject.getId(), userId);

        return ProjectMapper.toResponse(savedProject);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getUserProjects(Long userId) {
        log.info("[PROJECT] Fetching all projects for user with id: {}", userId);

        List<Project> projects = projectRepository.findByUserId(userId);
        log.info("[PROJECT] Found {} projects for user with id: {}", projects.size(), userId);

        return projects.stream()
                .map(ProjectMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(Long projectId, Long userId) {
        log.info("[PROJECT] Fetching project with id: {} for user: {}", projectId, userId);

        Project project = projectRepository.findByIdAndUserId(projectId, userId)
                .orElseThrow(() -> {
                    log.warn("[PROJECT] Project not found with id: {} for user: {}", projectId, userId);
                    return new ProjectNotFoundException("Project not found with id: " + projectId);
                });

        log.debug("[PROJECT] Project found: {} (tasks: {})", project.getTitle(), project.getTotalTasks());
        return ProjectMapper.toResponse(project);
    }

    @Transactional
    public ProjectResponse updateProject(Long projectId, ProjectRequest request, Long userId) {
        log.info("[PROJECT] Updating project with id: {} for user: {}", projectId, userId);

        Project project = projectRepository.findByIdAndUserId(projectId, userId)
                .orElseThrow(() -> {
                    log.warn("[PROJECT] Project not found with id: {} for user: {}", projectId, userId);
                    return new ProjectNotFoundException("Project not found with id: " + projectId);
                });

        String oldTitle = project.getTitle();
        project.setTitle(request.getTitle());
        project.setDescription(request.getDescription());

        Project updatedProject = projectRepository.save(project);
        log.info("[PROJECT] Project updated successfully: id={}, old title='{}', new title='{}'",
            projectId, oldTitle, updatedProject.getTitle());

        return ProjectMapper.toResponse(updatedProject);
    }

    @Transactional
    public void deleteProject(Long projectId, Long userId) {
        log.info("[PROJECT] Deleting project with id: {} for user: {}", projectId, userId);

        Project project = projectRepository.findByIdAndUserId(projectId, userId)
                .orElseThrow(() -> {
                    log.warn("[PROJECT] Project not found with id: {} for user: {}", projectId, userId);
                    return new ProjectNotFoundException("Project not found with id: " + projectId);
                });

        int taskCount = project.getTotalTasks();
        projectRepository.delete(project);
        log.info("[PROJECT] Project deleted successfully: id={}, title='{}', tasks deleted: {}",
            projectId, project.getTitle(), taskCount);
    }

    @Transactional(readOnly = true)
    public ProjectProgressResponse getProjectProgress(Long projectId, Long userId) {
        log.info("[PROJECT] Fetching progress for project with id: {} for user: {}", projectId, userId);

        Project project = projectRepository.findByIdAndUserId(projectId, userId)
                .orElseThrow(() -> {
                    log.warn("[PROJECT] Project not found with id: {} for user: {}", projectId, userId);
                    return new ProjectNotFoundException("Project not found with id: " + projectId);
                });

        ProjectProgressResponse progress = ProjectMapper.toProgressResponse(project);
        log.info("[PROJECT] Progress for project '{}': {}/{} tasks completed ({}%)",
            project.getTitle(), progress.getCompletedTasks(), progress.getTotalTasks(),
            String.format("%.2f", progress.getProgressPercentage()));
        return progress;
    }
}

