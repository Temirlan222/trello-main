package com.example.trellomain.dto.tasklist;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTaskListRequest(@NotBlank String name, @NotNull Long boardId) {
}
