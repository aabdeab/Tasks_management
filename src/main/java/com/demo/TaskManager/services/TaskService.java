package com.demo.TaskManager.services;

import com.demo.TaskManager.common.exceptions.ResourceNotFoundException;
import com.demo.TaskManager.dtos.TaskRequest;
import com.demo.TaskManager.dtos.TaskResponse;
import com.demo.TaskManager.entities.Project;
import com.demo.TaskManager.entities.Task;
import com.demo.TaskManager.repositories.ProjectRepository;
import com.demo.TaskManager.repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public TaskResponse createTask(Long projectId, TaskRequest request, Long userId) {
        Project project = projectRepository.findByIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .dueDate(request.getDueDate())
                .completed(false)
                .project(project)
                .build();

        Task savedTask = taskRepository.save(task);
        return mapToResponse(savedTask);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getProjectTasks(Long projectId, Long userId) {
        // Verify project belongs to user
        projectRepository.findByIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        List<Task> tasks = taskRepository.findByProjectId(projectId);
        return tasks.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long projectId, Long taskId, Long userId) {
        // Verify project belongs to user
        projectRepository.findByIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        Task task = taskRepository.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        return mapToResponse(task);
    }

    @Transactional
    public TaskResponse updateTask(Long projectId, Long taskId, TaskRequest request, Long userId) {
        // Verify project belongs to user
        projectRepository.findByIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        Task task = taskRepository.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());

        Task updatedTask = taskRepository.save(task);
        return mapToResponse(updatedTask);
    }

    @Transactional
    public TaskResponse markTaskAsCompleted(Long projectId, Long taskId, Long userId) {
        // Verify project belongs to user
        projectRepository.findByIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        Task task = taskRepository.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        task.setCompleted(true);
        Task updatedTask = taskRepository.save(task);
        return mapToResponse(updatedTask);
    }

    @Transactional
    public void deleteTask(Long projectId, Long taskId, Long userId) {
        // Verify project belongs to user
        projectRepository.findByIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        Task task = taskRepository.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        taskRepository.delete(task);
    }

    private TaskResponse mapToResponse(Task task) {
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

