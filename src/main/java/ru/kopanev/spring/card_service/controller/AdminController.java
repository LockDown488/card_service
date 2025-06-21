package ru.kopanev.spring.card_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.kopanev.spring.card_service.dto.card.CardEditDto;
import ru.kopanev.spring.card_service.dto.card.CardReadDto;
import ru.kopanev.spring.card_service.dto.user.UserEditDto;
import ru.kopanev.spring.card_service.dto.user.UserReadDto;
import ru.kopanev.spring.card_service.filter.CardFilter;
import ru.kopanev.spring.card_service.service.CardService;
import ru.kopanev.spring.card_service.service.UserService;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final CardService cardService;
    private final UserService userService;

    @PostMapping("/cards/create/{userId}")
    public ResponseEntity<CardReadDto> createCard(@PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cardService.createCard(userId));
    }

    @PutMapping("/cards/update")
    public ResponseEntity<CardReadDto> updateCard(@RequestBody CardEditDto cardEditDto) {
        return ResponseEntity.ok(cardService.updateCard(cardEditDto));
    }

    @GetMapping("/cards/{cardId}")
    public ResponseEntity<CardReadDto> getCardById(@PathVariable Long cardId) {
        return ResponseEntity.ok(cardService.getCardById(cardId));
    }

    @PostMapping("/cards/block/{cardId}")
    public ResponseEntity<CardReadDto> blockCard(@PathVariable Long cardId) {
        return ResponseEntity.ok(cardService.blockCard(cardId));
    }

    @PostMapping("/cards/active/{cardId}")
    public ResponseEntity<CardReadDto> activateCard(@PathVariable Long cardId) {
        return ResponseEntity.ok(cardService.activeCard(cardId));
    }

    @PostMapping("/cards/delete/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long cardId) {
        cardService.deleteCard(cardId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/cards")
    public ResponseEntity<CardReadDto> setCardDailyLimit(@RequestParam Long cardId, @RequestParam BigDecimal limit) {
        return ResponseEntity.ok(cardService.setCardDailyLimit(cardId, limit));
    }

    @GetMapping("/cards")
    public Page<CardReadDto> getCards(@ModelAttribute CardFilter cardFilter, Pageable pageable) {
        return cardService.getCards(cardFilter, pageable);
    }

    @GetMapping("/cards/{userId}")
    public ResponseEntity<UserReadDto> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PutMapping("/users/update")
    public ResponseEntity<UserReadDto> updateUser(@RequestBody UserEditDto  userEditDto) {
        return ResponseEntity.ok(userService.updateUser(userEditDto));
    }

    @PostMapping("/users/delete/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users")
    public Page<UserReadDto> getUsers(Pageable pageable) {
        return userService.getUsers(pageable);
    }
}
