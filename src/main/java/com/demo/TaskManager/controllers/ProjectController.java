package com.demo.TaskManager.controllers;

import com.demo.TaskManager.dtos.ProjectProgressResponse;
import com.demo.TaskManager.dtos.ProjectRequest;
import com.demo.TaskManager.dtos.ProjectResponse;
import com.demo.TaskManager.entities.User;
import com.demo.TaskManager.services.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(
            @Valid @RequestBody ProjectRequest request,
            @AuthenticationPrincipal User user) {
        ProjectResponse response = projectService.createProject(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects(@AuthenticationPrincipal User user) {
        List<ProjectResponse> projects = projectService.getUserProjects(user.getId());
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        ProjectResponse project = projectService.getProjectById(id, user.getId());
        return ResponseEntity.ok(project);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequest request,
            @AuthenticationPrincipal User user) {
        ProjectResponse response = projectService.updateProject(id, request, user.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        projectService.deleteProject(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/progress")
    public ResponseEntity<ProjectProgressResponse> getProjectProgress(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        ProjectProgressResponse progress = projectService.getProjectProgress(id, user.getId());
        return ResponseEntity.ok(progress);
    }
}

