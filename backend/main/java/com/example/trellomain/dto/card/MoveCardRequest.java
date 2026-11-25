package com.example.trellomain.dto.card;

import jakarta.validation.constraints.NotNull;

public record MoveCardRequest(@NotNull Long taskListId) {
}
