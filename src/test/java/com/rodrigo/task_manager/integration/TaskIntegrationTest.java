package com.rodrigo.task_manager.integration;

import com.rodrigo.task_manager.model.Task;
import com.rodrigo.task_manager.repository.TaskRepository;
import com.rodrigo.task_manager.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
public class TaskIntegrationTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    public void setUp() {
        taskRepository.deleteAll();
    }

    @Test
    public void testSaveTask() {
        Task task = new Task();
        task.setStatus("teste status");
        task.setTitle("teste titulo");
        task.setDescription("descrição teste");

        Task savedTask = taskService.save(task);

        System.out.println(savedTask.getId());
        System.out.println(savedTask.getTitle());
        System.out.println(savedTask.getDescription());
        System.out.println(savedTask.getCreatedAt());

        assertNotNull(savedTask);
        assertNotNull(savedTask.getCreatedAt());
        assertNotNull(savedTask.getId());
        assertEquals("teste titulo", savedTask.getTitle());
        assertEquals("teste status", savedTask.getStatus());

    }

}
