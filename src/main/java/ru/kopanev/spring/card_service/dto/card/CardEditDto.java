package ru.kopanev.spring.card_service.dto.card;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import ru.kopanev.spring.card_service.dto.user.UserSimpleDto;
import ru.kopanev.spring.card_service.enums.CardStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class CardEditDto {

    @NotNull(message = "ID обязателен для редактирования")
    private Long id;

    @Pattern(regexp = "\\d{10,}", message = "Номер карты должен быть не короче 10 цифр")
    private String cardNumber;

    @Valid
    private UserSimpleDto user;

    @FutureOrPresent(message = "Дата окончания должна быть сегодня или в будущем")
    private LocalDate expiryDate;

    private CardStatus status;
    private BigDecimal dailyLimit;
    private Boolean isBlockRequested;

    @JsonIgnore
    public void setId(Long id) {
        this.id = id;
    }
}
