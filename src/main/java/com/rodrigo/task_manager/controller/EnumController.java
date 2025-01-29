package com.rodrigo.task_manager.controller;

import com.rodrigo.task_manager.enuns.Status;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/enums")
@CrossOrigin(origins = "http://localhost:3000")
public class EnumController {

    @GetMapping("/status-task")
    public Status[] listarStatusTask() {
        return Status.values();
    }
}
