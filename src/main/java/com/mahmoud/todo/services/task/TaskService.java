package com.mahmoud.todo.services.task;


import com.mahmoud.todo.DTOs.Mapper.TaskMapper;
import com.mahmoud.todo.DTOs.task.CreateTaskRequest;
import com.mahmoud.todo.DTOs.task.TaskResponseDTO;
import com.mahmoud.todo.DTOs.task.TaskSummary;
import com.mahmoud.todo.DTOs.task.UpdateTaskRequest;

import com.mahmoud.todo.domain.exceptions.RessourceNotFoundException;
import com.mahmoud.todo.domain.task.Task;
import com.mahmoud.todo.domain.task.security.TaskAuthorizationService;
import com.mahmoud.todo.domain.user.AppUser;
import com.mahmoud.todo.domain.user.Role;
import com.mahmoud.todo.repos.AppUserRepo;
import com.mahmoud.todo.repos.TaskRepo;
import com.mahmoud.todo.security.AppUserDetails;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepo taskRepo;
    private final TaskAuthorizationService taskAuth;
    private final AppUserRepo appUserRepo;


    public TaskService(TaskRepo taskRepo, TaskAuthorizationService taskAuth, AppUserRepo appUserRepo) {
        this.taskRepo = taskRepo;
        this.taskAuth = taskAuth;
        this.appUserRepo = appUserRepo;
    }



    @Transactional
    public TaskResponseDTO createTask(CreateTaskRequest createTaskRequest, String owner) {

        Task task= Task.builder()
                .taskName(createTaskRequest.getTaskName())
                .taskDescription(createTaskRequest.getTaskDescription())
                .ownerUsername(owner)
                .createdAt(LocalDateTime.now())
                .build();

        taskRepo.save(task);
        return TaskMapper.toTaskResponseDTO(task);

    }


    public List<TaskSummary> getTasks(String owner){
        return taskRepo.findByOwnerUsernameAndDeletedAtIsNull(owner).stream().map(t->TaskMapper.toTaskSummary(t)).toList();
    }

    public TaskResponseDTO getTaskById(String id,String owner){
        Task task=  taskRepo.findByTaskId(id).orElseThrow(()-> new RessourceNotFoundException("Task not found"));

        if(task.getDeletedAt()!=null){
            throw new RessourceNotFoundException("Task not found or deleted");
        }


        taskAuth.canAccessTask(task,owner);

        return TaskMapper.toTaskResponseDTO(task);



    }

    @Transactional
    public TaskSummary updateTask(String  id, UpdateTaskRequest request,String owner){

        Task task=taskRepo.findByTaskId(id).orElseThrow(()-> new RessourceNotFoundException("Task not found"));

        if(task.getDeletedAt()!=null){
            throw new RessourceNotFoundException("Task not found or deleted");
        }
        taskAuth.canModifyTask(task,owner);

        task.update(request);

        taskRepo.save(task);
        return TaskMapper.toTaskSummary(task);
    }

    @Transactional
    public TaskSummary deleteTaskById(String id,String owner){
        Task task= taskRepo.findByTaskId(id).orElseThrow(()-> new RessourceNotFoundException("Task not found"));


        taskAuth.canModifyTask(task,owner);

        task.delete();

        taskRepo.save(task);
        return TaskMapper.toTaskSummary(task);
    }

    @Transactional
    public TaskSummary restoreTaskById(String id,String owner){
        Task task= taskRepo.findByTaskId(id).orElseThrow(()-> new RessourceNotFoundException("Task not found"));



        taskAuth.canRestoreTask(task,owner);

        //in the future i should test if the deleted task is within the restore period or not (the period is defined by the business logic)

        task.restore();

        taskRepo.save(task);
        return TaskMapper.toTaskSummary(task);

    }

    public List<TaskSummary> getDeletedTasks(String owner){
        AppUser currentUser = appUserRepo.findByUsername(owner).orElseThrow(()-> new RessourceNotFoundException("User not found"));

        if(currentUser.getRole() == Role.ADMIN){
            List<Task> tasks = taskRepo.findByDeletedAtIsNotNull();
            return tasks.stream().map(t->TaskMapper.toTaskSummary(t)).toList();
        }
        List<Task> tasks = taskRepo.findByOwnerUsernameAndDeletedAtIsNotNull(owner);

        return tasks.stream().map(t->TaskMapper.toTaskSummary(t)).toList();

    }



}
