package ru.kopanev.spring.card_service.mapper;

import org.springframework.stereotype.Component;
import ru.kopanev.spring.card_service.dto.card.CardSimpleDto;
import ru.kopanev.spring.card_service.dto.transaction.TransactionSimpleDto;
import ru.kopanev.spring.card_service.dto.user.UserSimpleDto;
import ru.kopanev.spring.card_service.entity.Card;
import ru.kopanev.spring.card_service.entity.Transaction;
import ru.kopanev.spring.card_service.entity.User;

import java.util.List;

@Component
public class SimpleDtoMapper {

    public UserSimpleDto toUserSimpleDto(User user) {
        return new UserSimpleDto(user.getId(), user.getEmail(), user.getRole());
    }

    public TransactionSimpleDto toTransactionSimpleDto(Transaction transaction) {
        return new TransactionSimpleDto(transaction.getId(), transaction.getAmount(), transaction.getTimestamp());
    }

    public CardSimpleDto toCardSimpleDto(Card card) {
        return new CardSimpleDto(card.getId(), card.getCardNumber());
    }

    public List<TransactionSimpleDto> toTransactionSimpleDto(List<Transaction> transactions) {
        return transactions.stream()
                .map(this::toTransactionSimpleDto)
                .toList();
    }

    public List<CardSimpleDto> toCardSimpleDto(List<Card> cards) {
        return cards.stream()
                .map(this::toCardSimpleDto)
                .toList();
    }
}
