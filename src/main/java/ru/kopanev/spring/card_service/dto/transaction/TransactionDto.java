package ru.kopanev.spring.card_service.dto.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransactionDto {

    private Long id;
    private Long cardId;
    private BigDecimal amount;
    private LocalDate timestamp;

    @JsonIgnore
    public void setId(Long id) {
        this.id = id;
    }
}
