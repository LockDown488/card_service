package ru.kopanev.spring.card_service.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.kopanev.spring.card_service.dto.card.CardEditDto;
import ru.kopanev.spring.card_service.dto.card.CardReadDto;
import ru.kopanev.spring.card_service.entity.Card;
import ru.kopanev.spring.card_service.entity.User;
import ru.kopanev.spring.card_service.enums.CardStatus;
import ru.kopanev.spring.card_service.filter.CardFilter;
import ru.kopanev.spring.card_service.mapper.CardMapper;
import ru.kopanev.spring.card_service.repository.CardRepository;
import ru.kopanev.spring.card_service.repository.specification.CardSpecification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CardService {

    private static final Random RANDOM = new Random();
    private static final long EXPIRED_TIME = 5;

    private final CardRepository cardRepository;
    private final UserService userService;
    private final CardMapper cardMapper;

    @Transactional
    public CardReadDto createCard(Long userId) {
        User user = userService.findUserById(userId);
        String cardNumber = generateCardNumber();

        while (cardRepository.existsByCardNumber(cardNumber)) {
            cardNumber = generateCardNumber();
        }

        LocalDate expiryDate = LocalDate.now().plusYears(EXPIRED_TIME);
        CardStatus status = CardStatus.ACTIVE;

        Card card = Card.builder()
                .cardNumber(cardNumber)
                .user(user)
                .expiryDate(expiryDate)
                .status(status)
                .build();

        cardRepository.save(card);

        return cardMapper.toDto(card);
    }

    @Transactional
    public CardReadDto updateCard(CardEditDto cardEditDto) {
        Card card = findCardById(cardEditDto.getId());
        Card updatedCard = cardRepository.save(cardMapper.update(cardEditDto, card));

        return cardMapper.toDto(updatedCard);
    }

    @Transactional
    public CardReadDto blockCard(Long cardId) {
        Card card = findCardById(cardId);
        card.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card);

        return cardMapper.toDto(card);
    }

    @Transactional
    public CardReadDto activeCard(Long cardId) {
        Card card = findCardById(cardId);
        card.setStatus(CardStatus.ACTIVE);
        cardRepository.save(card);

        return cardMapper.toDto(card);
    }

    @Transactional
    public void deleteCard(Long cardId) {
        cardRepository.deleteById(cardId);
    }

    @Transactional
    public CardReadDto setCardDailyLimit(Long cardId, BigDecimal dailyLimit) {
        Card card = findCardById(cardId);
        card.setDailyLimit(dailyLimit);

        return cardMapper.toDto(cardRepository.save(card));
    }

    public Page<CardReadDto> getCards(CardFilter cardFilter, Pageable pageable) {
        return cardRepository.findAll(CardSpecification.filterBy(cardFilter), pageable).map(cardMapper::toDto);
    }

    public Card findCardById(Long cardId) {
        return cardRepository.findById(cardId).orElseThrow(() -> new EntityNotFoundException("Card not found"));
    }

    public CardReadDto getCardById(Long cardId) {
        return cardMapper.toDto(findCardById(cardId));
    }

    private String generateCardNumber() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            stringBuilder.append(RANDOM.nextInt(10));
        }

        return stringBuilder.toString();
    }
}
