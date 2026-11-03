package com.example.trellomain.dto.board;

import com.example.trellomain.dto.tasklist.TaskListResponse;

import java.util.List;

public record BoardResponse(Long id, String name, List<TaskListResponse> lists) {
}
