package ru.kopanev.spring.card_service.dto.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionSimpleDto(Long id, BigDecimal amount, LocalDate timestamp) {
}
