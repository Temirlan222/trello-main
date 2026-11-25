package com.example.trellomain.dto.board;

import jakarta.validation.constraints.NotBlank;

public record CreateBoardRequest(@NotBlank String name) {
}
