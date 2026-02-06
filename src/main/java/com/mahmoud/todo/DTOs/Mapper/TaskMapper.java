package com.mahmoud.todo.DTOs.Mapper;

import com.mahmoud.todo.DTOs.task.DeletedTaskResponseDTO;
import com.mahmoud.todo.DTOs.task.TaskResponseDTO;
import com.mahmoud.todo.DTOs.task.TaskSummary;
import com.mahmoud.todo.domain.task.Task;

public class TaskMapper {

    public TaskMapper() {}

    public static TaskResponseDTO toTaskResponseDTO(Task task){
        return TaskResponseDTO.builder()
                .taskName(task.getTaskName())
                .taskDescription(task.getTaskDescription())
                .taskStatus(task.getTaskStatus())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
    public static DeletedTaskResponseDTO toDeletedTaskResponseDTO(Task task){
        return DeletedTaskResponseDTO.builder()
                .taskName(task.getTaskName())
                .taskDescription(task.getTaskDescription())
                .taskStatus(task.getTaskStatus())
                .deletedAt(task.getCreatedAt())
                .build();
    }
    public static TaskSummary toTaskSummary(Task task){
        return TaskSummary.builder()
                .taskName(task.getTaskName())
                .taskStatus(task.getTaskStatus())
                .build();
    }

}
