package com.mahmoud.todo.DTOs.task;


import lombok.Data;

@Data
public class CreateTaskRequest {
    private String taskName;

    private String taskDescription;

    public CreateTaskRequest(String taskName, String taskDescription) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
    }
}
