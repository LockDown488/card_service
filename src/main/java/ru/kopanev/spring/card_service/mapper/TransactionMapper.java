package ru.kopanev.spring.card_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.kopanev.spring.card_service.dto.transaction.TransactionDto;
import ru.kopanev.spring.card_service.entity.Card;
import ru.kopanev.spring.card_service.entity.Transaction;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TransactionMapper {

    @Mapping(target = "card", ignore = true)
    Transaction toEntity(TransactionDto dto);

    @Mapping(source = "card.id", target = "cardId")
    TransactionDto toDto(Transaction entity);

    default Transaction toEntityWithCard(TransactionDto dto, Card card) {
        Transaction transaction = toEntity(dto);
        transaction.setCard(card);
        return transaction;
    }
}
