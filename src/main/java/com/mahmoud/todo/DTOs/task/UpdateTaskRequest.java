package com.mahmoud.todo.DTOs.task;


import com.mahmoud.todo.domain.task.TaskStatus;
import lombok.Data;

@Data
public class UpdateTaskRequest {
    private String taskName;
    private String taskDescription;
    private TaskStatus taskStatus=TaskStatus.TODO;

}
