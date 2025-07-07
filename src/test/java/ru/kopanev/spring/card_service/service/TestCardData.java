package ru.kopanev.spring.card_service.service;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class TestCardData {

    private final Long userId = 1L;
    private final Long cardId = 1L;
    private final Long transactionId = 1L;
    private final Long currentUserId = 2L;
    private final Long senderCardId = 5L;
    private final Long receiverCardId = 6L;
    private final BigDecimal payment = BigDecimal.valueOf(500); // сумма перевода
    private final BigDecimal oldDailyLimit = BigDecimal.valueOf(10000);
    private final BigDecimal newDailyLimit = BigDecimal.valueOf(20000);
    private final BigDecimal successDailyLimit = BigDecimal.valueOf(1000); // для кейса успешной транзакции
    private final BigDecimal failedDailyLimit = BigDecimal.valueOf(400); // для кейса неудачной транзакции, когда сумма перевода больше
    private final BigDecimal successBalance = BigDecimal.valueOf(1000);
    private final BigDecimal failedBalance = BigDecimal.valueOf(400);
    private final String cardNumber = "1234567890";

}
