package ru.kopanev.spring.card_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.kopanev.spring.card_service.dto.card.CardReadDto;
import ru.kopanev.spring.card_service.dto.transaction.TransactionDto;
import ru.kopanev.spring.card_service.security.UserDetailsImpl;
import ru.kopanev.spring.card_service.service.CardService;
import ru.kopanev.spring.card_service.service.UserService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
public class UserController {

    private final CardService cardService;

    @GetMapping("/cards")
    public List<CardReadDto> getCards(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getId();

        return cardService.getUserCards(userId);
    }

    @PostMapping("/cards/{cardId}/request-block")
    public ResponseEntity<?> requestBlockCard(@PathVariable Long cardId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return cardService.requestBlockCard(cardId, userDetails);
    }

    @GetMapping("/cards/{cardId}/transactions")
    public List<TransactionDto> getCardTransactionList(
            @PathVariable Long cardId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return cardService.getCardTransactionList(cardId, userDetails.getId());
    }

    @PostMapping("/cards/make-transfer")
    public ResponseEntity<?> makeTransfer(
            @RequestParam Long senderCardId,
            @RequestParam Long receiverCardId,
            @RequestParam BigDecimal payment,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return cardService.makeTransfer(senderCardId, receiverCardId, payment, userDetails.getId());
    }
}
