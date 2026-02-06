package com.mahmoud.todo.domain.task.security;


import com.mahmoud.todo.domain.exceptions.NotAuthorizedException;
import com.mahmoud.todo.domain.task.Task;
import com.mahmoud.todo.domain.user.AppUser;
import com.mahmoud.todo.domain.user.Role;
import com.mahmoud.todo.repos.AppUserRepo;
import org.springframework.stereotype.Component;

@Component
public class TaskAuthorizationService {
    private final AppUserRepo appUserRepo;

    public TaskAuthorizationService(AppUserRepo appUserRepo) {
        this.appUserRepo = appUserRepo;
    }

    public void canAccessTask(Task task, String currentUser){
        if(isAdmin(currentUser)) return;
        if(!isOwner(task, currentUser)){
            System.out.println("is owner check for current user : "+ currentUser+": "+isOwner(task, currentUser));
            throw new NotAuthorizedException("You are not authorized to access this task");
        }
    }

    public void canModifyTask(Task task, String currentUser){
        if(isAdmin(currentUser)) return;
        if(!isOwner(task, currentUser)){
            throw new NotAuthorizedException("You are not authorized to modify this task");
        }
    }

    public void canRestoreTask(Task task, String currentUser){
        if(isAdmin(currentUser)) return;
        if(!isOwner(task, currentUser)){
            throw new NotAuthorizedException("You are not authorized to restore this task");
        }
    }

    public boolean isAdmin(String username){
        AppUser user = appUserRepo.findByUsername(username).orElseThrow(()-> new NotAuthorizedException("User not found"));
        return user.getRole()== Role.ADMIN;
    }

    public boolean isOwner(Task task, String username){
        AppUser user = appUserRepo.findByUsername(username).orElseThrow(()-> new NotAuthorizedException("User not found"));
        return task.getOwnerUsername().equals(username);
    }
}
