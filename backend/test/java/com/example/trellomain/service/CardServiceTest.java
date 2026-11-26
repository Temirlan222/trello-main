package com.example.trellomain.service;

import com.example.trellomain.dto.card.MoveCardRequest;
import com.example.trellomain.dto.card.UpdateTitleCardRequest;
import com.example.trellomain.entity.Board;
import com.example.trellomain.entity.Card;
import com.example.trellomain.entity.TaskList;
import com.example.trellomain.repository.CardRepository;
import com.example.trellomain.repository.TaskListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CardServiceTest {
    private CardRepository cardRepository;
    private TaskListRepository taskListRepository;
    private CardService cardService;
    private Board board;
    private TaskList source;
    private Card card;

    @BeforeEach
    void setUp() {
        cardRepository = mock(CardRepository.class);
        taskListRepository = mock(TaskListRepository.class);
        cardService = new CardService(taskListRepository, cardRepository);

        board = new Board(1L, "Демо", null);
        source = new TaskList("В работе", board, false);
        card = new Card(42L, "Написать тесты");
        card.setTaskList(source);

        when(cardRepository.findById(42L)).thenReturn(Optional.of(card));
    }

    @Test
    void movesCardWithinTheSameBoard() {
        TaskList target = new TaskList("Готово", board, true);
        when(taskListRepository.findById(11L)).thenReturn(Optional.of(target));
        when(cardRepository.save(card)).thenReturn(card);

        Card result = cardService.moveCard(42L, new MoveCardRequest(11L));

        assertSame(target, result.getTaskList());
        verify(cardRepository).save(card);
    }

    @Test
    void rejectsMovingCardToAnotherBoard() {
        Board otherBoard = new Board(2L, "Другая доска", null);
        TaskList target = new TaskList("Готово", otherBoard, true);
        when(taskListRepository.findById(11L)).thenReturn(Optional.of(target));

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> cardService.moveCard(42L, new MoveCardRequest(11L))
        );

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        assertSame(source, card.getTaskList());
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void rejectsBlankTitleWhenRenamingCard() {
        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> cardService.updateTitle(42L, new UpdateTitleCardRequest("   "))
        );

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        assertEquals("Написать тесты", card.getTitle());
        verify(cardRepository, never()).save(any(Card.class));
    }
}
