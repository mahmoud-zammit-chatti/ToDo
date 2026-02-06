package com.mahmoud.todo.repos;

import com.mahmoud.todo.domain.task.Task;
import com.mahmoud.todo.domain.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TaskRepo extends JpaRepository<Task, Long> {

    Optional<Task> findByTaskId(String id);


    List<Task> findByDeletedAtIsNotNull();

    List<Task> findByOwnerUsernameAndDeletedAtIsNull(String owner);

    List<Task> findByOwnerUsernameAndDeletedAtIsNotNull(String owner);
}
