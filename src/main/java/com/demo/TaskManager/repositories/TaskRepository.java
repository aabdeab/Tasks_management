package com.demo.TaskManager.repositories;

import com.demo.TaskManager.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProjectId(Long projectId);
    Optional<Task> findByIdAndProjectId(Long id, Long projectId);
    void deleteByIdAndProjectId(Long id, Long projectId);
}

