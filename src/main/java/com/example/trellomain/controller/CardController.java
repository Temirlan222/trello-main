package com.example.trellomain.controller;

import com.example.trellomain.dto.card.CardResponse;
import com.example.trellomain.dto.card.CreateCardRequest;
import com.example.trellomain.dto.card.MoveCardRequest;
import com.example.trellomain.dto.card.UpdateDescriptionCardRequest;
import com.example.trellomain.dto.card.UpdateDueDateCardRequest;
import com.example.trellomain.dto.card.UpdatePriorityCardRequest;
import com.example.trellomain.dto.card.UpdateTitleCardRequest;
import com.example.trellomain.service.CardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
public class CardController {
    private final CardService cardService;

    @Autowired
    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping
    public List<CardResponse> getAllCards() {
        return cardService.getAllCards();
    }

    @GetMapping("/{id}")
    public CardResponse getCard(@PathVariable Long id) {
        return cardService.getCard(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CardResponse createCard(@Valid @RequestBody CreateCardRequest card) {
        return cardService.toResponse(cardService.createCard(card));
    }

    @PatchMapping("/{id}/title")
    public CardResponse updateTitleCard(@PathVariable Long id, @RequestBody UpdateTitleCardRequest request) {
        return cardService.toResponse(cardService.updateTitle(id, request));
    }

    @PatchMapping("/{id}/description")
    public CardResponse updateCardDescription(@PathVariable Long id,
                                              @RequestBody UpdateDescriptionCardRequest request) {
        return cardService.toResponse(cardService.updateDescription(id, request));
    }

    @PatchMapping("/{id}/due-date")
    public CardResponse updateCardDueDate(@PathVariable Long id,
                                          @RequestBody UpdateDueDateCardRequest request) {
        return cardService.toResponse(cardService.updateDueDate(id, request));
    }

    @PatchMapping("/{id}/priority")
    public CardResponse updateCardPriority(@PathVariable Long id,
                                           @RequestBody @Valid UpdatePriorityCardRequest request) {
        return cardService.toResponse(cardService.updatePriority(id, request));
    }

    @PatchMapping("/{id}/move")
    public CardResponse moveCard(@PathVariable Long id,
                                 @Valid @RequestBody MoveCardRequest request) {
        return cardService.toResponse(cardService.moveCard(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
    }

}
