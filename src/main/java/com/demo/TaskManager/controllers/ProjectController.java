package com.demo.TaskManager.controllers;

import com.demo.TaskManager.dtos.ProjectProgressResponse;
import com.demo.TaskManager.dtos.ProjectRequest;
import com.demo.TaskManager.dtos.ProjectResponse;
import com.demo.TaskManager.security.SecurityUser;
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
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ProjectResponse> createProject(
            @Valid @RequestBody ProjectRequest request,
            @AuthenticationPrincipal SecurityUser securityUser) {
        ProjectResponse response = projectService.createProject(request, securityUser.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects(@AuthenticationPrincipal SecurityUser securityUser) {
        List<ProjectResponse> projects = projectService.getUserProjects(securityUser.getUserId());
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(
            @PathVariable Long id,
            @AuthenticationPrincipal SecurityUser securityUser) {
        ProjectResponse project = projectService.getProjectById(id, securityUser.getUserId());
        return ResponseEntity.ok(project);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequest request,
            @AuthenticationPrincipal SecurityUser securityUser) {
        ProjectResponse response = projectService.updateProject(id, request, securityUser.getUserId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(
            @PathVariable Long id,
            @AuthenticationPrincipal SecurityUser securityUser) {
        projectService.deleteProject(id, securityUser.getUserId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/progress")
    public ResponseEntity<ProjectProgressResponse> getProjectProgress(
            @PathVariable Long id,
            @AuthenticationPrincipal SecurityUser securityUser) {
        ProjectProgressResponse progress = projectService.getProjectProgress(id, securityUser.getUserId());
        return ResponseEntity.ok(progress);
    }
}

