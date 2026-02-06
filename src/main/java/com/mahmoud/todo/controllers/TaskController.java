package com.mahmoud.todo.controllers;

import com.mahmoud.todo.DTOs.task.CreateTaskRequest;
import com.mahmoud.todo.DTOs.task.TaskResponseDTO;
import com.mahmoud.todo.DTOs.task.TaskSummary;
import com.mahmoud.todo.DTOs.task.UpdateTaskRequest;
import com.mahmoud.todo.domain.user.AppUser;
import com.mahmoud.todo.security.AppUserDetails;
import com.mahmoud.todo.services.task.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/task")
    public ResponseEntity<TaskResponseDTO> createTask(@RequestBody CreateTaskRequest task, @AuthenticationPrincipal AppUserDetails currentUser){
      String owner= currentUser.getUsername();
        return ResponseEntity.ok()
                .body(taskService.createTask(task,owner));
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<TaskSummary>> getTasks(@AuthenticationPrincipal AppUserDetails currentUser){
String owner= currentUser.getUsername();
        return ResponseEntity.ok()
                .body(taskService.getTasks(owner));
    }

    @GetMapping("/task/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable String id,@AuthenticationPrincipal AppUserDetails currentUser){
        String owner= currentUser.getUsername();
        return ResponseEntity.ok()
                .body(taskService.getTaskById(id,owner));
    }
    @PutMapping("/task/{id}")
    public ResponseEntity<TaskSummary> updateTask(@PathVariable String id, @RequestBody UpdateTaskRequest task, @AuthenticationPrincipal AppUserDetails currentUser){
      String owner= currentUser.getUsername();
        return ResponseEntity.ok()
                .body(taskService.updateTask(id,task,owner));
    }
    @DeleteMapping("/task/{id}")
    public ResponseEntity<TaskSummary> deleteTask(@PathVariable String id,@AuthenticationPrincipal AppUserDetails currentUser){
        String owner= currentUser.getUsername();
        return ResponseEntity.ok()
                .body(taskService.deleteTaskById(id,owner));
    }

    @PutMapping("/task/restore/{id}")
    public ResponseEntity<TaskSummary> restoreTask(@PathVariable String id,@AuthenticationPrincipal AppUserDetails currentUser){
String owner= currentUser.getUsername();
        return ResponseEntity.ok()
                .body(taskService.restoreTaskById(id,owner));
    }
    @GetMapping("/tasks/deleted")
    public ResponseEntity<List<TaskSummary>> getDeletedTasks(@AuthenticationPrincipal AppUserDetails currentUser){
String owner= currentUser.getUsername();
        return ResponseEntity.ok()
                .body(taskService.getDeletedTasks(owner));
    }

}
