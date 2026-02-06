package com.mahmoud.todo.DTOs.task;

import com.mahmoud.todo.domain.task.TaskStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskSummary {
    private String taskName;
    private TaskStatus taskStatus;



}
