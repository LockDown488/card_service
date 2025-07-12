package ru.kopanev.spring.card_service.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kopanev.spring.card_service.dto.transaction.TransactionDto;
import ru.kopanev.spring.card_service.entity.Transaction;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TransactionMapper {

    private final SimpleDtoMapper simpleDtoMapper;

    public Transaction toEntity(TransactionDto dto) {
        if ( dto == null ) {
            return null;
        }

        return Transaction.builder()
                .id(dto.getId())
                .amount(dto.getAmount())
                .timestamp(dto.getTimestamp())
                .build();
    }

    public TransactionDto toDto(Transaction entity) {
        if ( entity == null ) {
            return null;
        }

        return TransactionDto.builder()
                .id(entity.getId())
                .card(simpleDtoMapper.toCardSimpleDto(entity.getCard()))
                .amount(entity.getAmount())
                .timestamp(entity.getTimestamp())
                .build();
    }

    public List<TransactionDto> toTransactionDtos(List<Transaction> entities) {
        return entities.stream()
                .map(this::toDto)
                .toList();
    }
}
