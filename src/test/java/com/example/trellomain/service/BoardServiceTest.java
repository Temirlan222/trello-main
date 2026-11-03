package com.example.trellomain.service;

import com.example.trellomain.dto.card.SmartCardResponse;
import com.example.trellomain.entity.Board;
import com.example.trellomain.entity.Card;
import com.example.trellomain.entity.Priority;
import com.example.trellomain.entity.TaskList;
import com.example.trellomain.repository.BoardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BoardServiceTest {
    private BoardRepository boardRepository;
    private BoardService boardService;

    @BeforeEach
    void setUp(){
        boardRepository = mock(BoardRepository.class);
        TaskListService taskListService = mock(TaskListService.class);
        boardService = new BoardService(boardRepository,taskListService);
    }

    @Test
    void smartGetReturnsEmptyListForEmptyBoard(){
        Board board = new Board(1L,"Demo",null);
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));

        List< SmartCardResponse> result = boardService.smartGet(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void smartGetReturnsNotFoundForMissingBoard() {
        when(boardRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> boardService.smartGet(999L)
        );

        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
    }

    @Test
    void smartGetSkipsCardsFromDoneList() {
        Board board = new Board(1L, "Demo", null);
        TaskList activeList = new TaskList("To do", board, false);
        TaskList doneList = new TaskList("Done", board, true);
        activeList.getCards().add(new Card(1L, "Active task"));
        doneList.getCards().add(new Card(2L, "Finished task"));
        board.getLists().add(activeList);
        board.getLists().add(doneList);
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));

        List<SmartCardResponse> result = boardService.smartGet(1L);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).id());
    }

    @Test
    void smartGetSortsByScoreThenIdAndReturnsAtMostThreeCards() {
        Board board = new Board(1L, "Demo", null);
        TaskList taskList = new TaskList("To do", board, false);

        Card low = new Card(2L, "Low priority");
        low.setPriority(Priority.LOW);
        Card mediumWithHigherId = new Card(3L, "Medium id 3");
        Card mediumWithLowerId = new Card(1L, "Medium id 1");
        Card high = new Card(5L, "High priority");
        high.setPriority(Priority.HIGH);

        taskList.getCards().add(low);
        taskList.getCards().add(mediumWithHigherId);
        taskList.getCards().add(mediumWithLowerId);
        taskList.getCards().add(high);
        board.getLists().add(taskList);
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));

        List<SmartCardResponse> result = boardService.smartGet(1L);

        assertEquals(3, result.size());
        assertEquals(5L, result.get(0).id());
        assertEquals(1L, result.get(1).id());
        assertEquals(3L, result.get(2).id());
    }

    @Test
    void smartGetReturnsScore() {
        Board board = new Board(1L, "Demo", null);
        TaskList taskList = new TaskList("Test", board, false);
        Card card = new Card(5L, "Task");
        card.setTaskList(taskList);
        card.setPriority(Priority.HIGH);
        card.setDueDate(LocalDate.now().minusDays(1));
        taskList.getCards().add(card);
        board.getLists().add(taskList);
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));
        List<SmartCardResponse> result = boardService.smartGet(1L);

        assertEquals(1, result.size());

        SmartCardResponse response = result.get(0);
        assertEquals(5L, response.id());
        assertEquals(80, response.score());


    }

}
