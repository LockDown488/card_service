package ru.kopanev.spring.card_service.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CardNumberConverterTest {

    private static final String TEST_KEY = "1234567890123456";
    private static final String WRONG_KEY = "6543210987654321";
    private static final String CARD_NUMBER = "1234567890";
    private static final String INVALID_INPUT = "not_base64";

    private CardNumberConverter converter;

    @BeforeEach
    void setUp() {
        System.setProperty("EncryptionSecretKey", TEST_KEY);
        converter = new CardNumberConverter();
    }

    @Test
    void convertToDatabaseColumn_andBack_success() {
        String encrypted = converter.convertToDatabaseColumn(CARD_NUMBER);

        assertNotNull(encrypted);
        assertNotEquals(CARD_NUMBER, encrypted);

        String decrypted = converter.convertToEntityAttribute(encrypted);
        assertEquals(CARD_NUMBER, decrypted);
    }

    @Test
    void convertToDatabaseColumn_nullValue() {
        assertThrows(RuntimeException.class, () -> converter.convertToDatabaseColumn(null));
    }

    @Test
    void convertToEntityAttribute_invalidInput() {
        assertThrows(RuntimeException.class, () -> converter.convertToEntityAttribute(INVALID_INPUT));
    }

    @Test
    void convertToDatabaseColumn_wrongKey() {
        String encrypted = converter.convertToDatabaseColumn(CARD_NUMBER);

        System.setProperty("EncryptionSecretKey", WRONG_KEY);
        converter = new CardNumberConverter();
        assertThrows(RuntimeException.class, () -> converter.convertToEntityAttribute(encrypted));
    }
}
