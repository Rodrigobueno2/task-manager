package com.rodrigo.task_manager.service;

import com.rodrigo.task_manager.exceptions.ResourceNotFoundException;
import com.rodrigo.task_manager.model.Task;
import com.rodrigo.task_manager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public Task save(Task task) {
        task.setCreatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }

    public void deleteById(Long id) {
        if(taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("Task not found");
        }
    }

    public Task updateTask(long id, Task updatedTask) {
        //vou encontrar a tarefa buscando pelo id
        Optional<Task> optionalTask = taskRepository.findById(id);

        //verificar se essa tarefa existe
        if(optionalTask.isPresent()) {
            //Capturar para uma variavel a task
            Task task = optionalTask.get();

            //setar as novas informações nessa task
            task.setDescription(updatedTask.getDescription());
            task.setStatus(updatedTask.getStatus());
            task.setTitle(updatedTask.getTitle());

            //Salvar essa task
            return taskRepository.save(task);

        } else {
            throw new ResourceNotFoundException("Task not found with id: " + id);
        }
    }

    public Task findById(Long id) {
        //vou pesquisar no repositorio uma tarefa que tem o mesmo id do parametro
        Optional<Task> task = taskRepository.findById(id);

        //vou verificar se ele existe, se sim vou retornar, se não dou uma excessão
        if(task.isPresent()) {
            return task.get();
        } else {
            throw new ResourceNotFoundException("Task not found with id: " + id);
        }
    }

}
