package com.mahmoud.todo.repos;

import com.mahmoud.todo.models.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepos extends JpaRepository<Task, Long> {
}
