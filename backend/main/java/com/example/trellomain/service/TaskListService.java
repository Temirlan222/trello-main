package com.example.trellomain.service;

import com.example.trellomain.dto.card.CardResponse;
import com.example.trellomain.dto.tasklist.CreateTaskListRequest;
import com.example.trellomain.dto.tasklist.TaskListResponse;
import com.example.trellomain.entity.Board;
import com.example.trellomain.entity.Card;
import com.example.trellomain.entity.TaskList;
import com.example.trellomain.repository.BoardRepository;
import com.example.trellomain.repository.TaskListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskListService {
    private final TaskListRepository taskListRepository;
    private final BoardRepository boardRepository;

    @Autowired
    public TaskListService(TaskListRepository taskListRepository, BoardRepository boardRepository) {
        this.taskListRepository = taskListRepository;
        this.boardRepository = boardRepository;
    }

    @Transactional(readOnly = true)
    public List<TaskListResponse> getAllTaskLists() {
        List<TaskListResponse> responses = new ArrayList<>();
        for (TaskList taskList : taskListRepository.findAll()) {
            responses.add(toResponse(taskList));
        }
        return responses;
    }

    @Transactional(readOnly = true)
    public TaskListResponse getTaskList(Long id) {
        TaskList taskList = taskListRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return toResponse(taskList);
    }

    public TaskList createTaskList(CreateTaskListRequest taskList) {
        Board board = boardRepository.findById(taskList.boardId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        TaskList newTaskList = new TaskList(taskList.name(), board);
        return taskListRepository.save(newTaskList);
    }

    public TaskList createLastTaskList(CreateTaskListRequest taskList) {
        Board board = boardRepository.findById(taskList.boardId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        List<TaskList> existingLists = taskListRepository.findByBoard_Id(taskList.boardId());

        for (TaskList existingList : existingLists) {
            if (Boolean.TRUE.equals(existingList.getDone())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT);
            }
        }
        TaskList newTaskList = new TaskList(taskList.name(), board, true);
        return taskListRepository.save(newTaskList);
    }

    public TaskListResponse toResponse(TaskList taskList) {
        List<CardResponse> answers = new ArrayList<>();

        for (Card card : taskList.getCards()) {
            CardResponse answer = new CardResponse(
                    card.getId(), card.getTitle(), card.getDescription(), card.getDueDate(), card.getPriority()
            );
            answers.add(answer);
        }
        return new TaskListResponse(taskList.getName(), taskList.getId(), answers, taskList.getDone());
    }
}
