package ru.kopanev.spring.card_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kopanev.spring.card_service.repository.CardRepository;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
}
