package com.example.trellomain.dto.card;

import com.example.trellomain.entity.Priority;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateCardRequest(@NotBlank @Size(min=0, max = 120, message = "The title must be no more than 120 characters long.") String title,
                                        @Nullable String description, Priority priority,
                                        @Nullable LocalDate due_date, @NotNull Long taskListId) {
}
