package com.example.trellomain.dto.card;

import com.example.trellomain.entity.Priority;

import java.time.LocalDate;

public record CardResponse(Long id, String title, String description, LocalDate dueDate, Priority priority) {
}
