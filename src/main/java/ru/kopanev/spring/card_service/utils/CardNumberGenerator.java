package ru.kopanev.spring.card_service.utils;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class CardNumberGenerator {

    private static final Random RANDOM = new Random();

    public String generateCardNumber() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            stringBuilder.append(RANDOM.nextInt(10));
        }

        return stringBuilder.toString();
    }
}
