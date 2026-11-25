package com.example.trellomain.service;

import com.example.trellomain.dto.card.CardResponse;
import com.example.trellomain.dto.card.CreateCardRequest;
import com.example.trellomain.dto.card.MoveCardRequest;
import com.example.trellomain.dto.card.UpdateDescriptionCardRequest;
import com.example.trellomain.dto.card.UpdateDueDateCardRequest;
import com.example.trellomain.dto.card.UpdatePriorityCardRequest;
import com.example.trellomain.dto.card.UpdateTitleCardRequest;
import com.example.trellomain.entity.Board;
import com.example.trellomain.entity.Card;
import com.example.trellomain.entity.TaskList;
import com.example.trellomain.repository.CardRepository;
import com.example.trellomain.repository.TaskListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class CardService {
    private final TaskListRepository taskListRepository;
    private final CardRepository cardRepository;

    @Autowired
    public CardService(TaskListRepository taskListRepository, CardRepository cardRepository) {
        this.taskListRepository = taskListRepository;
        this.cardRepository = cardRepository;
    }

    @Transactional(readOnly = true)
    public List<CardResponse> getAllCards() {
        List<CardResponse> responses = new ArrayList<>();
        for (Card card : cardRepository.findAll()) {
            responses.add(toResponse(card));
        }
        return responses;
    }

    @Transactional(readOnly = true)
    public CardResponse getCard(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return toResponse(card);
    }

    public Card createCard(CreateCardRequest card) {
        TaskList taskList = taskListRepository.findById(card.taskListId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Card newCard = new Card(taskList, card.title(), card.description(), card.priority(), card.due_date());
        return cardRepository.save(newCard);
    }

    public CardResponse toResponse(Card card) {
        CardResponse response = new CardResponse(
                card.getId(), card.getTitle(), card.getDescription(), card.getDueDate(), card.getPriority()
        );
        return response;
    }

    public Card updateTitle(Long id, UpdateTitleCardRequest request) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (request.title() == null || request.title().isBlank() || request.title().length() > 120) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        card.setTitle(request.title());

        return cardRepository.save(card);
    }

    public Card updateDescription(Long id, UpdateDescriptionCardRequest request) {
        Card card = cardRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (request.description() != null) {
            card.setDescription(request.description());
        }

        return cardRepository.save(card);
    }

    public Card updateDueDate(Long id, UpdateDueDateCardRequest request) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        card.setDueDate(request.dueDate());
        return cardRepository.save(card);
    }

    public Card updatePriority(Long id, UpdatePriorityCardRequest request) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        card.setPriority(request.priority());
        return cardRepository.save(card);
    }

    public Card moveCard(Long id, MoveCardRequest request) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        TaskList currentTaskList = card.getTaskList();
        Board currentBoard = currentTaskList.getBoard();
        TaskList targetTaskList = taskListRepository.findById(request.taskListId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Board targetBoard = targetTaskList.getBoard();

        if (targetBoard.getId().equals(currentBoard.getId())) {
            card.setTaskList(targetTaskList);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return cardRepository.save(card);
    }

    public void deleteCard(Long id) {
        if (cardRepository.findById(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        cardRepository.deleteById(id);
    }



}
