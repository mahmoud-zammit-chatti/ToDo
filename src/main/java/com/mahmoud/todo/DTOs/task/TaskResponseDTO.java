package com.mahmoud.todo.DTOs.task;

import com.mahmoud.todo.domain.task.TaskStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TaskResponseDTO {
    private String taskName;
    private String taskDescription;
    private TaskStatus taskStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
