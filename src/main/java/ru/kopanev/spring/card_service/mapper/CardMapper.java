package ru.kopanev.spring.card_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import ru.kopanev.spring.card_service.dto.card.CardEditDto;
import ru.kopanev.spring.card_service.dto.card.CardReadDto;
import ru.kopanev.spring.card_service.entity.Card;
import ru.kopanev.spring.card_service.entity.Transaction;
import ru.kopanev.spring.card_service.entity.User;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {UserMapper.class, TransactionMapper.class})
public interface CardMapper {

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    Card toEntity(CardReadDto dto);

    CardReadDto toDto(Card entity);

    Card update(CardEditDto dto, @MappingTarget Card entity);

    default Card toFullEntity(CardReadDto dto, User user, List<Transaction> transactions) {
        Card card = toEntity(dto);
        card.setUser(user);
        card.setTransactions(transactions);
        return card;
    }
}
