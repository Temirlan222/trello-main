package com.example.trellomain.controller;

import com.example.trellomain.dto.tasklist.CreateTaskListRequest;
import com.example.trellomain.dto.tasklist.TaskListResponse;
import com.example.trellomain.service.TaskListService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tasklists")
public class TaskListController {
    private final TaskListService taskListService;

    @Autowired
    public TaskListController(TaskListService taskListService) {
        this.taskListService = taskListService;
    }

    @GetMapping
    public List<TaskListResponse> getAllTaskLists() {
        return taskListService.getAllTaskLists();
    }

    @GetMapping("/{id}")
    public TaskListResponse getTaskList(@PathVariable Long id) {
        return taskListService.getTaskList(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskListResponse createTaskList(@Valid @RequestBody CreateTaskListRequest createTaskListRequest) {
        return taskListService.toResponse(taskListService.createTaskList(createTaskListRequest));
    }

    @PostMapping("/last")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskListResponse createLastTaskList(@Valid @RequestBody CreateTaskListRequest createLastTaskListRequest) {
        return taskListService.toResponse(taskListService.createLastTaskList(createLastTaskListRequest));
    }
}
