package ru.kopanev.spring.card_service.exception;

import io.jsonwebtoken.JwtException;

public class ExpiredJwtException extends JwtException {

    public ExpiredJwtException(String message) {
        super(message);
    }
}
