package com.example.trellomain.dto.card;

import com.example.trellomain.entity.Priority;

public record SmartCardResponse (Long id, String title, Priority priority, Integer score, String reason) {
}
