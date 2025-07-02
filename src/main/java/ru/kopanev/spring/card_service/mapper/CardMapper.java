package ru.kopanev.spring.card_service.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kopanev.spring.card_service.dto.card.CardCreateDto;
import ru.kopanev.spring.card_service.dto.card.CardEditDto;
import ru.kopanev.spring.card_service.dto.card.CardReadDto;
import ru.kopanev.spring.card_service.entity.Card;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CardMapper {

    private final SimpleDtoMapper simpleDtoMapper;

    public Card toEntity(CardCreateDto dto) {
        if ( dto == null ) {
            return null;
        }

        return Card.builder()
                .cardNumber(dto.getCardNumber())
                .expiryDate(dto.getExpiryDate())
                .status(dto.getStatus())
                .build();
    }

    public CardReadDto toDto(Card entity) {
        if ( entity == null ) {
            return null;
        }

        return CardReadDto.builder()
                .id(entity.getId())
                .cardNumber(entity.getCardNumber())
                .user(simpleDtoMapper.toUserSimpleDto(entity.getUser()))
                .expiryDate(entity.getExpiryDate())
                .status(entity.getStatus())
                .balance(entity.getBalance())
                .dailyLimit(entity.getDailyLimit())
                .isBlockRequested(entity.getIsBlockRequested())
                .transactions(simpleDtoMapper.toTransactionSimpleDto(entity.getTransactions()))
                .build();
    }

    public Card update(CardEditDto source, Card target) {
        if ( source == null ) {
            return target;
        }

        if ( source.getExpiryDate() != null ) {
            target.setExpiryDate(source.getExpiryDate());
        }

        if ( source.getStatus() != null ) {
            target.setStatus(source.getStatus());
        }

        if ( source.getDailyLimit() != null ) {
            target.setDailyLimit(source.getDailyLimit());
        }

        if ( source.getIsBlockRequested() != null) {
            target.setIsBlockRequested(source.getIsBlockRequested());
        }

        return target;
    }

    public List<CardReadDto> toCardReadDtos(List<Card> entities) {
        return entities.stream()
                .map(this::toDto)
                .toList();
    }
}
