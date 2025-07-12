package ru.kopanev.spring.card_service.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.kopanev.spring.card_service.dto.card.CardEditDto;
import ru.kopanev.spring.card_service.dto.card.CardReadDto;
import ru.kopanev.spring.card_service.dto.transaction.TransactionDto;
import ru.kopanev.spring.card_service.entity.Card;
import ru.kopanev.spring.card_service.entity.User;
import ru.kopanev.spring.card_service.enums.CardStatus;
import ru.kopanev.spring.card_service.filter.CardFilter;
import ru.kopanev.spring.card_service.mapper.CardMapper;
import ru.kopanev.spring.card_service.mapper.TransactionMapper;
import ru.kopanev.spring.card_service.repository.CardRepository;
import ru.kopanev.spring.card_service.repository.specification.CardSpecification;
import ru.kopanev.spring.card_service.security.UserDetailsImpl;
import ru.kopanev.spring.card_service.utils.CardNumberGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {

    private static final long EXPIRED_TIME = 5;

    private final CardRepository cardRepository;
    private final UserService userService;
    private final CardMapper cardMapper;
    private final TransactionMapper transactionMapper;
    private final CardNumberGenerator cardNumberGenerator;

    @Transactional
    public CardReadDto createCard(Long userId) {
        User user = userService.findUserById(userId);
        String cardNumber = cardNumberGenerator.generateCardNumber();

        while (cardRepository.existsByCardNumber(cardNumber)) {
            cardNumber = cardNumberGenerator.generateCardNumber();
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

    @Transactional
    public ResponseEntity<?> requestBlockCard(Long cardId, UserDetailsImpl userDetails) {
        Card card = findCardById(cardId);

        if (!card.getUser().getId().equals(userDetails.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        card.setIsBlockRequested(true);
        cardRepository.save(card);

        return ResponseEntity.ok("Block card request is created");
    }

    @Transactional
    public ResponseEntity<?> makeTransfer(
            Long senderCardId,
            Long receiverCardId,
            BigDecimal payment,
            Long userId) {
        Card sender = findCardById(senderCardId);
        Card receiver = findCardById(receiverCardId);

        if (!sender.getUser().getId().equals(userId) || !receiver.getUser().getId().equals(userId)) {
            return new ResponseEntity<>("Both cards do not belong to user with ID" + userId, HttpStatus.FORBIDDEN);
        }

        if (payment.compareTo(sender.getBalance()) > 0) {
            return new ResponseEntity<>("Insufficient funds", HttpStatus.FORBIDDEN);
        }

        if (payment.compareTo(sender.getDailyLimit()) > 0) {
            return new ResponseEntity<>("Daily transfer limit spent", HttpStatus.FORBIDDEN);
        }

        sender.setBalance(sender.getBalance().subtract(payment));
        receiver.setBalance(receiver.getBalance().add(payment));

        cardRepository.save(sender);
        cardRepository.save(receiver);

        return ResponseEntity.ok("Payment successful");
    }

    public Page<CardReadDto> getCards(CardFilter cardFilter, Pageable pageable) {
        return cardRepository.findAll(CardSpecification.filterBy(cardFilter), pageable).map(cardMapper::toDto);
    }

    public List<CardReadDto> getUserCards(Long userId) {
        List<Card> cards = cardRepository.findByUserId(userId);

        return cardMapper.toCardReadDtos(cards);
    }

    public Card findCardById(Long cardId) {
        return cardRepository.findById(cardId).orElseThrow(() -> new EntityNotFoundException("Card not found"));
    }

    public CardReadDto getCardById(Long cardId) {
        return cardMapper.toDto(findCardById(cardId));
    }

    public List<TransactionDto> getCardTransactionList(Long cardId, Long userId) {
        Card card = findCardById(cardId);

        if (!card.getUser().getId().equals(userId)) {
            return Collections.emptyList();
        }

        return transactionMapper.toTransactionDtos(card.getTransactions());
    }
}
