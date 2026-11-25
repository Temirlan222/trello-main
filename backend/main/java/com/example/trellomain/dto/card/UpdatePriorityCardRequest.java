package com.example.trellomain.dto.card;

import com.example.trellomain.entity.Priority;
import jakarta.validation.constraints.NotNull;

public record UpdatePriorityCardRequest(@NotNull Priority priority) {
}
