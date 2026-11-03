package com.example.trellomain.controller;

import com.example.trellomain.dto.board.BoardResponse;
import com.example.trellomain.dto.board.CreateBoardRequest;
import com.example.trellomain.dto.card.SmartCardResponse;
import com.example.trellomain.service.BoardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
public class BoardController {
    private final BoardService boardService;
    @Autowired
    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping
    public List<BoardResponse> getAllBoards() {
        return boardService.getAllBoards();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BoardResponse createBoard(@Valid @RequestBody CreateBoardRequest board) {
        return boardService.toResponse(boardService.createBoard(board));
    }

    @GetMapping("/{id}")
    public BoardResponse getBoardWithDetails(@PathVariable Long id) {
        return boardService.toResponse(boardService.returnBoard(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBoard(@PathVariable Long id) {
        boardService.deleteBoard(id);
    }

    @GetMapping("/{id}/focus")
    public List<SmartCardResponse> smartGet(@PathVariable Long id) {
        return boardService.smartGet(id);
    }

}
