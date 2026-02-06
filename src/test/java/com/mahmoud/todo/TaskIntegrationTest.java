package com.mahmoud.todo;


import com.mahmoud.todo.domain.task.Task;
import com.mahmoud.todo.domain.user.AppUser;
import com.mahmoud.todo.domain.user.Role;
import com.mahmoud.todo.repos.AppUserRepo;
import com.mahmoud.todo.repos.TaskRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TaskIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AppUserRepo appUserRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TaskRepo taskRepo;

    @BeforeEach
    void setup() {
        AppUser user = AppUser.builder()
                .username("mahmoud")
                .password(passwordEncoder.encode("123456"))
                .role(Role.USER)
                .build();


        AppUser user1 = AppUser.builder()
                .username("rihem")
                .password(passwordEncoder.encode("123456"))
                .role(Role.USER)
                .build();

        appUserRepo.save(user);
        appUserRepo.save(user1);
    }


    @Test
    @WithUserDetails(value = "mahmoud", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void should_create_task_successfully() throws Exception {

        mockMvc.perform(
                        post("/api/v1/task")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                        {
                          "taskName": "Study Spring",
                          "taskDescription": "Integration tests"
                        }
                    """)
                )
                .andExpect(status().isOk());
    }
    @Test
    //@WithUserDetails(value = "mahmoud", setupBefore = TestExecutionEvent.TEST_EXECUTION)

    void should_fail_create_task_when_unauthenticated() throws Exception{
        mockMvc.perform(
                post("/api/v1/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "taskName": "Study Spring",
                          "taskDescription": "Integration tests"
                        }
                    """)
        ).andExpect(status().isUnauthorized());

    }

    @Test
    @WithUserDetails(value = "rihem", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void get_task_of_another_user() throws Exception {

               Task mahmoudTask = Task.builder()
                .taskName("Mahmoud Task")
                .taskDescription("Task for Mahmoud")
                .ownerUsername("mahmoud")
                .build();
        taskRepo.save(mahmoudTask);

        String taskId=mahmoudTask.getTaskId();

        mockMvc.perform(
                        get("/api/v1/task/"+taskId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = "mahmoud", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void delete_task_of_another_user() throws Exception {
        Task rihemTask = Task.builder()
                .taskName("Rihem Task")
                .taskDescription("Task for Rihem")
                .ownerUsername("rihem")
                .build();
        taskRepo.save(rihemTask);

        String taskId=rihemTask.getTaskId();

        mockMvc.perform(
                        delete("/api/v1/task/"+taskId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value="mahmoud", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void should_fail_get_task_not_found() throws Exception {
        mockMvc.perform(
                get("/api/v1/task/invalid-task-id")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound());

    }

    @Test
    @WithUserDetails(value="mahmoud", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void should_fail_get_deleted_task() throws Exception {
        Task task = Task.builder()
                .taskName("Deleted Task")
                .taskDescription("This task is deleted")
                .ownerUsername("mahmoud")
                .deletedAt(java.time.LocalDateTime.now())
                .build();
        taskRepo.save(task);

        String taskId=task.getTaskId();

        mockMvc.perform(
                get("/api/v1/task/"+taskId)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound());

    }

    @Test
    @WithUserDetails(value="mahmoud", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void should_get_own_task_successfully() throws Exception {
        Task task = Task.builder()
                .taskName("Own Task")
                .taskDescription("This is my own task")
                .ownerUsername("mahmoud")
                .build();
        taskRepo.save(task);

        String taskId=task.getTaskId();

        mockMvc.perform(
                get("/api/v1/task/"+taskId)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk());

    }

    @Test
    @WithUserDetails(value="mahmoud", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void should_update_own_task_successfully() throws Exception{

        Task task = Task.builder()
                .taskName("Task to Update")
                .taskDescription("This task will be updated")
                .ownerUsername("mahmoud")
                .build();
        taskRepo.save(task);

        mockMvc.perform(
                put("/api/v1/task/"+task.getTaskId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                        {
                                          "taskName": "Updated Task Name",
                                          "taskStatus": "IN_PROGRESS"
                                        }
                                """
                        )

        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.taskName").value("Updated Task Name"))
                .andExpect(jsonPath("$.taskStatus").value("IN_PROGRESS"));


    }

    @Test
    @WithUserDetails(value="mahmoud", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void forbidden_update_task_of_another_user() throws Exception {

        Task task = Task.builder()
                .taskName("Rihem's Task")
                .taskDescription("This task belongs to Rihem")
                .ownerUsername("rihem")
                .build();
        taskRepo.save(task);

        mockMvc.perform(
                put("/api/v1/task/" + task.getTaskId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                        {
                                          "taskName": "Malicious Update",
                                          "taskStatus": "DONE"
                                        }
                                """
                        )

        ).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value="mahmoud", setupBefore =TestExecutionEvent.TEST_EXECUTION)
    void bad_request_update_deleted_task() throws Exception {
        Task task = Task.builder()
                .taskName("Deleted Task")
                .taskDescription("This task is deleted")
                .ownerUsername("mahmoud")
                .deletedAt(java.time.LocalDateTime.now())
                .build();
        taskRepo.save(task);

        mockMvc.perform(
                put("/api/v1/task/" + task.getTaskId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                        {
                                          "taskName": "Attempted Update",
                                          "taskStatus": "DONE"
                                        }
                                """
                        )

        ).andExpect(status().isNotFound());
    }


    @Test
    @WithUserDetails(value="mahmoud", setupBefore =TestExecutionEvent.TEST_EXECUTION)
    void should_exclude_deleted_tasks_from_get_all() throws Exception {
        Task activeTask = Task.builder()
                .taskName("Active Task")
                .taskDescription("This task is active")
                .ownerUsername("mahmoud")
                .build();
        taskRepo.save(activeTask);

        Task deletedTask = Task.builder()
                .taskName("Deleted Task")
                .taskDescription("This task is deleted")
                .ownerUsername("mahmoud")
                .deletedAt(java.time.LocalDateTime.now())
                .build();
        taskRepo.save(deletedTask);

        mockMvc.perform(
                        get("/api/v1/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].taskName").value("Active Task"));
    }


}
