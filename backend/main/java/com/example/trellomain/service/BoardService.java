package com.example.trellomain.service;

import com.example.trellomain.dto.board.BoardResponse;
import com.example.trellomain.dto.board.CreateBoardRequest;
import com.example.trellomain.dto.card.SmartCardResponse;
import com.example.trellomain.dto.tasklist.TaskListResponse;
import com.example.trellomain.entity.Board;
import com.example.trellomain.entity.Card;
import com.example.trellomain.entity.Priority;
import com.example.trellomain.entity.TaskList;
import com.example.trellomain.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class BoardService {
    private final BoardRepository boardRepository;
    private final TaskListService taskListService;

    @Autowired
    public BoardService(BoardRepository boardRepository, TaskListService taskListService) {
        this.boardRepository = boardRepository;
        this.taskListService = taskListService;
    }

    public List<BoardResponse> getAllBoards() {
        List<Board> boards = boardRepository.findAll();
        List<BoardResponse> answers = new ArrayList<>();
        for (Board board : boards) {
            answers.add(toResponse(board));
        }
        return answers;
    }

    public Board createBoard(CreateBoardRequest board) {
        Board board1 = new Board(null, board.name(), null);

        return boardRepository.save(board1);
    }

    public void deleteBoard(Long id) {
        if (boardRepository.findById(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        boardRepository.deleteById(id);
    }

    public Board returnBoard(Long id) {
        return boardRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public BoardResponse toResponse(Board board) {
        List<TaskListResponse> answers = new ArrayList<>();

        for (TaskList taskList : board.getLists()) {
            TaskListResponse answer = taskListService.toResponse(taskList);
            answers.add(answer);
        }
        return new BoardResponse(board.getId(), board.getName(), answers);
    }


    @Transactional(readOnly = true)
    public List<SmartCardResponse> smartGet(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        List<SmartCardResponse> responses = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (TaskList taskList : board.getLists()) {
            if (!Boolean.FALSE.equals(taskList.getDone())) {
                continue;
            }
            for (Card card : taskList.getCards()) {
                responses.add(toSmartCardResponse(card, today));
            }
        }

        responses.sort((a, b) -> {
            int byScore = Integer.compare(b.score(), a.score());
            if (byScore != 0) {
                return byScore;
            }
            return Long.compare(a.id(), b.id());
        });
        return new ArrayList<>(responses.subList(0, Math.min(3, responses.size())));
    }

    private SmartCardResponse toSmartCardResponse(Card card, LocalDate today) {
        int score = 0;
        String deadlineReason;

        if (card.getDueDate() == null) {
            deadlineReason = "none";
        } else {
            long days = ChronoUnit.DAYS.between(today, card.getDueDate());
            if (days < 0) {
                score += 50;
                deadlineReason = "overdue";
            } else if (days <= 2) {
                score += 30;
                deadlineReason = "in 0-2 days";
            } else if (days <= 7) {
                score += 10;
                deadlineReason = "in 3-7 days";
            } else {
                deadlineReason = "later";
            }
        }

        if (card.getPriority() == Priority.HIGH) {
            score += 30;
        } else if (card.getPriority() == Priority.MEDIUM) {
            score += 20;
        } else {
            score += 10;
        }

        String reason = "Priority: " + card.getPriority() + "; deadline: " + deadlineReason;
        return new SmartCardResponse(card.getId(), card.getTitle(), card.getPriority(), score, reason);
    }
}