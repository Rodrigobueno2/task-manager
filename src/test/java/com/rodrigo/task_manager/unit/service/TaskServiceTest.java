package com.rodrigo.task_manager.unit.service;

import com.rodrigo.task_manager.enuns.Status;
import com.rodrigo.task_manager.exceptions.ResourceNotFoundException;
import com.rodrigo.task_manager.model.Task;
import com.rodrigo.task_manager.repository.TaskRepository;
import com.rodrigo.task_manager.service.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    public void testSalvarTarefa() {
        //preparar o teste
        Task task = new Task();
        task.setTitle("teste");
        task.setDescription("Descrição");
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation ->{
            Task tarefaParaSalvar = invocation.getArgument(0);
            tarefaParaSalvar.setId(1L);
            tarefaParaSalvar.setCreatedAt(LocalDateTime.now());
            return tarefaParaSalvar;
        });

        //chamar o metodo salvar do service
        Task resultado = taskService.save(task);

        //testar
        assertNotNull(resultado);
        assertNotNull(resultado.getCreatedAt());
        assertEquals("teste",resultado.getTitle());
        assertEquals("Descrição",resultado.getDescription());
        verify(taskRepository, times(1)).save(task);

    }

    @Test
    public void testSalvarTarefa2() {
        //preparar o teste
        Task task = new Task();
        task.setTitle("teste");
        task.setDescription("Descrição");
        task.setCreatedAt(null);
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        //chamar o metodo salvar do service
        Task resultado = taskService.save(task);

        //testar
        assertNotNull(resultado);
        assertNotNull(resultado.getCreatedAt());
        assertEquals("teste",resultado.getTitle());
        assertEquals("Descrição",resultado.getDescription());
        verify(taskRepository, times(1)).save(task);

    }

    @Test
    public void testListarTodasTarefas() {
        // configurar
        Task task1 = new Task(1L,"tarefa1","descrição1", Status.FAZENDO,LocalDateTime.now());
        Task task2 = new Task(2L,"tarefa2","descrição2",Status.FAZENDO,LocalDateTime.now());
        when(taskRepository.findAll()).thenReturn(Arrays.asList(task1,task2));

        List<Task> tasks = taskService.findAll();

        assertNotNull(tasks);
        assertEquals(2, tasks.size());
        verify(taskRepository, times(1)).findAll();

    }

    @Test
    public void testBuscarTarefaQueExiste() {
        //Criando uma tarefa mockada
        Task task = new Task(1L,"tarefa1","descrição1",Status.FAZENDO,LocalDateTime.now());

        // Mockando o comportamento do repositório
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        // Chamando o método do serviço
        Optional<Task> resultado = Optional.ofNullable(taskService.findById(1L));


        // Verificando se a tarefa foi retornada corretamente
        assertTrue(resultado.isPresent());
        assertEquals("tarefa1", resultado.get().getTitle());

        // Verificando se o método do repositório foi chamado uma vez
        verify(taskRepository, times(1)).findById(1L);

    }

    @Test
    public void testBuscarTarefaQueNaoExiste() {

        //mockando o comportamento do repositório
        when(taskRepository.findById(2L)).thenReturn(Optional.empty());

        // Executar o método e verificar exceção
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> taskService.findById(2L));


        assertEquals("Task not found with id: " + 2L, exception.getMessage());

        verify(taskRepository, times(1)).findById(2L);

    }

    @Test
    public void testAtualizarTarefasQuandoExiste() {
        long id = 1L;

        Task existeTask = new Task();
        existeTask.setDescription("velha descrição");
        existeTask.setStatus(Status.FAZENDO);
        existeTask.setTitle("velho titulo");
        existeTask.setId(id);

        Task taskAtualizada = new Task();
        taskAtualizada.setDescription("nova descrição");
        taskAtualizada.setStatus(Status.PENDENTE);
        taskAtualizada.setTitle("novo titulo");

        when(taskRepository.findById(id)).thenReturn(Optional.of(existeTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task result = taskService.updateTask(id, taskAtualizada);

        assertNotNull(result);
        assertEquals("novo titulo", result.getTitle());
        assertEquals("nova descrição", result.getDescription());
        assertEquals(Status.PENDENTE, result.getStatus());

        verify(taskRepository, times(1)).findById(id); // Verifica que o findById foi chamado
        verify(taskRepository, times(1)).save(existeTask);
    }

    @Test
    public void testAtualizarTarefaQuandoTarefaNaoExiste() {
        long id = 1L;

        Task updatedTask = new Task();
        updatedTask.setTitle("novo titulo");
        updatedTask.setStatus(Status.CONCLUIDO);
        updatedTask.setDescription("nova descrição");

        // Simular o repositório retornando vazio
        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> taskService.updateTask(id, updatedTask));
        assertEquals("Task not found with id: " + id, exception.getMessage());

        verify(taskRepository, times(1)).findById(id); // Verifica que o findById foi chamado
        verify(taskRepository, never()).save(any(Task.class)); // Verifica que o save não foi chamado


    }

    @Test
    public void testDeletarTarefaByIdQuandoExiste() {
        Long id = 1L;

        when(taskRepository.existsById(id)).thenReturn(true);

        taskService.deleteById(id);

        verify(taskRepository, times(1)).deleteById(id);
    }

    @Test
    public void testDeletarTarefaByIdQuandoTarefaNaoExiste() {
        Long id = 999L;

        when(taskRepository.existsById(id)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            taskService.deleteById(id);
        });

        assertEquals("Task not found",exception.getMessage());
        verify(taskRepository, never()).deleteById(anyLong());

    }
}
