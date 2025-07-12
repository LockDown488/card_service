package ru.kopanev.spring.card_service.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


class CardNumberGeneratorTest {

    private final CardNumberGenerator generator = new CardNumberGenerator();

    @Test
    void generateCardNumber_lenIsTen() {
        String cardNumber = generator.generateCardNumber();
        assertEquals(10, cardNumber.length());
    }

    @Test
    void generateCardNumber_cardNumberUniq() {
        String cardNumber1 = generator.generateCardNumber();
        String cardNumber2 = generator.generateCardNumber();
        assertNotEquals(cardNumber1, cardNumber2);
    }
}
