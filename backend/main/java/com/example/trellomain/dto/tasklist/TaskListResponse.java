package com.example.trellomain.dto.tasklist;

import com.example.trellomain.dto.card.CardResponse;

import java.util.List;

public record TaskListResponse(String name, Long id, List<CardResponse> cards, Boolean isDone) {
}
