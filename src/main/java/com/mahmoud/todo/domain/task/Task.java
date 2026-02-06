package com.mahmoud.todo.domain.task;


import com.mahmoud.todo.DTOs.task.UpdateTaskRequest;
import com.mahmoud.todo.domain.exceptions.OperationNotPossibleException;
import com.mahmoud.todo.domain.user.AppUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Task {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(nullable = false, updatable = false)
    private String taskId;

    @Column(nullable = false)
    private String taskName;

    private String taskDescription;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TaskStatus taskStatus=TaskStatus.TODO;

    @Column(nullable = false)
    private String ownerUsername;

    private LocalDateTime deletedAt=null;

    private LocalDateTime createdAt=null;

    private LocalDateTime updatedAt=null;


    public void delete(){
        if(this.deletedAt!=null){
            throw new OperationNotPossibleException("Task is already deleted");
        }
        this.deletedAt=LocalDateTime.now();

    }
    public void restore(){
        if(this.deletedAt==null){
            throw new OperationNotPossibleException("Task is not deleted");
        }
        this.deletedAt=null;
    }
    public void update(UpdateTaskRequest updatedTask){

        this.taskName= updatedTask.getTaskName()==null?this.taskName : updatedTask.getTaskName();

        this.taskDescription= updatedTask.getTaskDescription()==null? this.taskDescription : updatedTask.getTaskDescription();
        this.taskStatus= updatedTask.getTaskStatus()==null? this.taskStatus: updatedTask.getTaskStatus();
        this.updatedAt=LocalDateTime.now();
    }



}
