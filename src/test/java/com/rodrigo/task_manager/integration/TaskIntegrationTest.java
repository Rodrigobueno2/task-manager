package com.rodrigo.task_manager.integration;

import com.rodrigo.task_manager.enuns.Status;
import com.rodrigo.task_manager.model.Task;
import com.rodrigo.task_manager.repository.TaskRepository;
import com.rodrigo.task_manager.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


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

    public void popularRepositorioTask() {
        Task task1 = new Task(null,"titulo 1","descrição 1",Status.AFAZER, LocalDateTime.now());
        Task task2 = new Task(null,"titulo 2","descrição 2",Status.CONCLUIDO, LocalDateTime.now());
        Task task3 = new Task(null,"titulo 3","descrição 3",Status.FAZENDO, LocalDateTime.now());
        Task task4 = new Task(null,"titulo 4","descrição 4",Status.PENDENTE, LocalDateTime.now());
        taskRepository.save(task1);
        taskRepository.save(task2);
        taskRepository.save(task3);
        taskRepository.save(task4);
    }

    @Test
    public void testSaveTask() {
        Task task = new Task();
        task.setStatus(Status.AFAZER);
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
        assertEquals(Status.AFAZER, savedTask.getStatus());

    }

    @Test
    public void testListarTasks() {
        popularRepositorioTask();

        List<Task> tasks = taskService.findAll();

        assertNotNull(tasks);
        assertEquals(4, tasks.size());
        assertEquals("titulo 1",tasks.get(0).getTitle());
        assertEquals("titulo 2",tasks.get(1).getTitle());
        assertEquals("titulo 3",tasks.get(2).getTitle());


    }

    @Test
    public void testBuscarTaskComSucesso() {
        popularRepositorioTask();

        Task taskExistente = taskService.findAll().get(0);

        Task taskEncontrada = taskService.findById(taskExistente.getId());

        assertNotNull(taskEncontrada);
        assertEquals(taskEncontrada.getId(),taskExistente.getId());
        assertEquals("titulo 1",taskEncontrada.getTitle());

    }

    @Test
    public void testBuscarTaskSemSucesso() {
        Long invalidId = 999L;

        // Verifica se a exceção é lançada
        RuntimeException exception = assertThrows(RuntimeException.class, () -> taskService.findById(invalidId));
        assertEquals("Task not found with id: " + invalidId, exception.getMessage());

    }

    @Test
    public void testDeletarTaskComSucesso() {
        popularRepositorioTask();

        Task taskParaDeletar = taskRepository.findAll().get(0);
        taskService.deleteById(taskParaDeletar.getId());

        assertEquals(3,taskRepository.findAll().size());

        assertFalse(taskRepository.findById(taskParaDeletar.getId()).isPresent());

    }

    @Test
    public void testDeletarTaskSemSucesso() {
        Long idInexistente = 999L;

        RuntimeException exception = assertThrows(RuntimeException.class, () -> taskService.deleteById(idInexistente));
        assertEquals("Task not found", exception.getMessage());

    }

    @Test
    public void testAtualizarTaskComSucesso() {
        popularRepositorioTask();

        Task taskParaAtualizar = taskRepository.findAll().get(0);
        taskParaAtualizar.setTitle("titulo alterado");
        taskParaAtualizar.setStatus(Status.FAZENDO);
        Task taskAlterado = taskService.updateTask(taskParaAtualizar.getId(),taskParaAtualizar);

        assertEquals("titulo alterado",taskAlterado.getTitle());
        assertEquals(Status.FAZENDO,taskAlterado.getStatus());
        assertEquals("descrição 1",taskAlterado.getDescription());

    }

    @Test
    public void testAtualizarTaskSemSucesso() {
        Long id = 99L;

        Task task = new Task(null,"titulo alterado","descrição alterado",Status.FAZENDO,null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> taskService.updateTask(id,task));
        assertEquals("Task not found with id: " + id, exception.getMessage());

    }



}
