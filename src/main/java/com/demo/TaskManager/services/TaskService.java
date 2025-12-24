package com.demo.TaskManager.services;

import com.demo.TaskManager.common.exceptions.ProjectNotFoundException;
import com.demo.TaskManager.common.exceptions.TaskNotFoundException;
import com.demo.TaskManager.dtos.TaskRequest;
import com.demo.TaskManager.dtos.TaskResponse;
import com.demo.TaskManager.entities.Project;
import com.demo.TaskManager.entities.Task;
import com.demo.TaskManager.mappers.TaskMapper;
import com.demo.TaskManager.repositories.ProjectRepository;
import com.demo.TaskManager.repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public TaskResponse createTask(Long projectId, TaskRequest request, Long userId) {
        log.info("[TASK] Creating new task '{}' for project: {} (user: {})",
            request.getTitle(), projectId, userId);

        Project project = projectRepository.findByIdAndUserId(projectId, userId)
                .orElseThrow(() -> {
                    log.warn("[TASK] Project not found with id: {} for user: {}", projectId, userId);
                    return new ProjectNotFoundException("Project not found with id: " + projectId);
                });

        Task task = TaskMapper.fromRequest(request, project);

        Task savedTask = taskRepository.save(task);
        log.info("[TASK] Task created successfully: id={}, title='{}', project='{}', due date: {}",
            savedTask.getId(), savedTask.getTitle(), project.getTitle(), savedTask.getDueDate());

        return TaskMapper.toResponse(savedTask);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getProjectTasks(Long projectId, Long userId) {
        log.info("[TASK] Fetching all tasks for project: {} (user: {})", projectId, userId);

        verifyProjectExistsForUser(projectId, userId);

        List<Task> tasks = taskRepository.findByProjectId(projectId);
        long completedCount = tasks.stream().filter(Task::isCompleted).count();

        log.info("[TASK] Found {} tasks for project {}: {} completed, {} pending",
            tasks.size(), projectId, completedCount, tasks.size() - completedCount);

        return tasks.stream()
                .map(TaskMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long projectId, Long taskId, Long userId) {
        log.info("[TASK] Fetching task with id: {} from project: {} (user: {})", taskId, projectId, userId);

        verifyProjectExistsForUser(projectId, userId);

        Task task = taskRepository.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> {
                    log.warn("[TASK] Task not found with id: {} in project: {}", taskId, projectId);
                    return new TaskNotFoundException("Task not found with id: " + taskId);
                });

        log.debug("[TASK] Task found: '{}', status: {}, due: {}",
            task.getTitle(), task.isCompleted() ? "completed" : "pending", task.getDueDate());

        return TaskMapper.toResponse(task);
    }

    @Transactional
    public TaskResponse updateTask(Long projectId, Long taskId, TaskRequest request, Long userId) {
        log.info("[TASK] Updating task with id: {} in project: {} (user: {})", taskId, projectId, userId);

        // Verify project belongs to user
        verifyProjectExistsForUser(projectId, userId);

        Task task = taskRepository.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> {
                    log.warn("[TASK] Task not found with id: {} in project: {}", taskId, projectId);
                    return new TaskNotFoundException("Task not found with id: " + taskId);
                });

        String oldTitle = task.getTitle();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());

        Task updatedTask = taskRepository.save(task);
        log.info("[TASK] Task updated successfully: id={}, old title='{}', new title='{}', due date: {}",
            taskId, oldTitle, updatedTask.getTitle(), updatedTask.getDueDate());

        return TaskMapper.toResponse(updatedTask);
    }

    @Transactional
    public TaskResponse markTaskAsCompleted(Long projectId, Long taskId, Long userId) {
        log.info("[TASK] Marking task as completed: id={}, project: {} (user: {})", taskId, projectId, userId);

        // Verify project belongs to user
        verifyProjectExistsForUser(projectId, userId);

        Task task = taskRepository.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> {
                    log.warn("[TASK] Task not found with id: {} in project: {}", taskId, projectId);
                    return new TaskNotFoundException("Task not found with id: " + taskId);
                });

        if (task.isCompleted()) {
            log.info("[TASK] Task {} was already completed", taskId);
        }

        task.setCompleted(true);
        Task updatedTask = taskRepository.save(task);
        log.info("[TASK] Task marked as completed successfully: id={}, title='{}'",
            taskId, updatedTask.getTitle());

        return TaskMapper.toResponse(updatedTask);
    }

    @Transactional
    public void deleteTask(Long projectId, Long taskId, Long userId) {
        log.info("[TASK] Deleting task with id: {} from project: {} (user: {})", taskId, projectId, userId);

        // Verify project belongs to user
        verifyProjectExistsForUser(projectId, userId);

        Task task = taskRepository.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> {
                    log.warn("[TASK] Task not found with id: {} in project: {}", taskId, projectId);
                    return new TaskNotFoundException("Task not found with id: " + taskId);
                });

        String taskTitle = task.getTitle();
        boolean wasCompleted = task.isCompleted();
        taskRepository.delete(task);
        log.info("[TASK] Task deleted successfully: id={}, title='{}', was completed: {}",
            taskId, taskTitle, wasCompleted);
    }


    /**
     * Vérifie que le projet existe et appartient à l'utilisateur
     * @param projectId id of the project
     * @param userId id of the user
     * @throws ProjectNotFoundException if the project does not exist or does not belong to the user
     */
    private void verifyProjectExistsForUser(Long projectId, Long userId) {
        projectRepository.findByIdAndUserId(projectId, userId)
                .orElseThrow(() -> {
                    log.warn("[TASK] Project not found with id: {} for user: {}", projectId, userId);
                    return new ProjectNotFoundException("Project not found with id: " + projectId);
                });
    }
}

