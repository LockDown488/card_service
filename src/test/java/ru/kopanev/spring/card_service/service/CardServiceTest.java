package ru.kopanev.spring.card_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.kopanev.spring.card_service.dto.card.CardEditDto;
import ru.kopanev.spring.card_service.dto.card.CardReadDto;
import ru.kopanev.spring.card_service.dto.transaction.TransactionDto;
import ru.kopanev.spring.card_service.dto.user.UserReadDto;
import ru.kopanev.spring.card_service.entity.Card;
import ru.kopanev.spring.card_service.entity.Transaction;
import ru.kopanev.spring.card_service.entity.User;
import ru.kopanev.spring.card_service.enums.CardStatus;
import ru.kopanev.spring.card_service.filter.CardFilter;
import ru.kopanev.spring.card_service.mapper.CardMapper;
import ru.kopanev.spring.card_service.mapper.TransactionMapper;
import ru.kopanev.spring.card_service.repository.CardRepository;
import ru.kopanev.spring.card_service.security.UserDetailsImpl;
import ru.kopanev.spring.card_service.utils.CardNumberGenerator;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    private final TestCardData data = new TestCardData();
    private final CardFilter filter = new CardFilter();

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserService userService;

    @Mock
    private CardMapper cardMapper;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private CardNumberGenerator cardNumberGenerator;

    @InjectMocks
    private CardService cardService;

    @Test
    void createCard_success() {
        User user = User.builder()
                .id(data.getUserId())
                .build();
        Card card = Card.builder()
                .user(user)
                .cardNumber(data.getCardNumber())
                .build();
        CardReadDto dto = CardReadDto.builder()
                .cardNumber(data.getCardNumber())
                .status(CardStatus.ACTIVE)
                .build();

        when(userService.findUserById(data.getUserId())).thenReturn(user);
        when(cardNumberGenerator.generateCardNumber()).thenReturn(data.getCardNumber());
        when(cardRepository.existsByCardNumber(data.getCardNumber())).thenReturn(false);
        when(cardRepository.save(any(Card.class))).thenReturn(card);
        when(cardMapper.toDto(any(Card.class))).thenReturn(dto);

        CardReadDto result = cardService.createCard(data.getUserId());

        assertEquals(dto, result);
        verify(cardRepository).save(any(Card.class));
        assertEquals(CardStatus.ACTIVE, result.getStatus());
    }

    @Test
    void createCard_userNotFound() {
        when(userService.findUserById(data.getUserId())).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> cardService.createCard(data.getUserId()));
    }

    @Test
    void updateCard_success() {
        Card card = Card.builder()
                .id(data.getCardId())
                .cardNumber(data.getCardNumber())
                .dailyLimit(data.getOldDailyLimit())
                .build();
        CardEditDto editDto = CardEditDto.builder()
                .id(data.getCardId())
                .cardNumber(data.getCardNumber())
                .dailyLimit(data.getNewDailyLimit())
                .build();
        Card updatedCard = Card.builder()
                .id(data.getCardId())
                .cardNumber(data.getCardNumber())
                .dailyLimit(data.getNewDailyLimit())
                .build();
        CardReadDto readDto = CardReadDto.builder()
                .id(data.getCardId())
                .cardNumber(data.getCardNumber())
                .dailyLimit(data.getNewDailyLimit())
                .build();

        when(cardRepository.findById(data.getCardId())).thenReturn(Optional.of(card));
        when(cardMapper.update(editDto, card)).thenReturn(updatedCard);
        when(cardRepository.save(any(Card.class))).thenReturn(updatedCard);
        when(cardMapper.toDto(any(Card.class))).thenReturn(readDto);

        CardReadDto result = cardService.updateCard(editDto);

        assertEquals(readDto, result);
        verify(cardMapper).update(any(CardEditDto.class), any(Card.class));
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void updateCard_cardNotFound() {
        CardEditDto editDto = CardEditDto.builder()
                .id(data.getCardId())
                .cardNumber(data.getCardNumber())
                .dailyLimit(data.getNewDailyLimit())
                .build();

        when(cardRepository.findById(data.getCardId())).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> cardService.updateCard(editDto));
    }

    @Test
    void blockCard_success() {
        Card card = Card.builder()
                .id(data.getCardId())
                .cardNumber(data.getCardNumber())
                .build();
        CardReadDto dto = CardReadDto.builder()
                .id(data.getCardId())
                .cardNumber(data.getCardNumber())
                .status(CardStatus.BLOCKED)
                .build();

        when(cardRepository.findById(data.getCardId())).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        when(cardMapper.toDto(any(Card.class))).thenReturn(dto);

        CardReadDto result = cardService.blockCard(data.getCardId());
        assertEquals(dto, result);
        assertEquals(CardStatus.BLOCKED, card.getStatus());
        verify(cardRepository).save(any(Card.class));
        verify(cardMapper).toDto(any(Card.class));
    }

    @Test
    void blockCard_cardNotFound() {
        when(cardRepository.findById(data.getCardId())).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> cardService.blockCard(data.getCardId()));
    }

    @Test
    void activeCard_success() {
        Card card = Card.builder()
                .id(data.getCardId())
                .cardNumber(data.getCardNumber())
                .build();
        CardReadDto dto = CardReadDto.builder()
                .id(data.getCardId())
                .cardNumber(data.getCardNumber())
                .status(CardStatus.ACTIVE)
                .build();

        when(cardRepository.findById(data.getCardId())).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        when(cardMapper.toDto(any(Card.class))).thenReturn(dto);

        CardReadDto result = cardService.activeCard(data.getCardId());
        assertEquals(dto, result);
        assertEquals(CardStatus.ACTIVE, card.getStatus());
        verify(cardRepository).save(any(Card.class));
        verify(cardMapper).toDto(any(Card.class));
    }

    @Test
    void activeCard_cardNotFound() {
        when(cardRepository.findById(data.getCardId())).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> cardService.activeCard(data.getCardId()));
    }

    @Test
    void deleteCard_success() {
        cardService.deleteCard(data.getCardId());

        verify(cardRepository).deleteById(data.getCardId());
    }

    @Test
    void setCardDailyLimit_success() {
        Card card = Card.builder()
                .id(data.getCardId())
                .cardNumber(data.getCardNumber())
                .build();
        CardReadDto dto = CardReadDto.builder()
                .id(data.getCardId())
                .cardNumber(data.getCardNumber())
                .dailyLimit(data.getNewDailyLimit())
                .build();

        when(cardRepository.findById(data.getCardId())).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        when(cardMapper.toDto(any(Card.class))).thenReturn(dto);

        CardReadDto result = cardService.setCardDailyLimit(data.getCardId(), data.getNewDailyLimit());
        assertEquals(dto, result);
        assertEquals(data.getNewDailyLimit(), result.getDailyLimit());
        verify(cardRepository).save(any(Card.class));
        verify(cardMapper).toDto(any(Card.class));
    }

    @Test
    void setCardDailyLimit_cardNotFound() {
        when(cardRepository.findById(data.getCardId())).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> cardService.setCardDailyLimit(
                data.getCardId(),
                data.getNewDailyLimit()
        ));
    }

    @Test
    void requestBlockCard_success() {
        User user = User.builder().id(data.getUserId()).build();
        Card card = Card.builder()
                .id(data.getCardId())
                .user(user)
                .isBlockRequested(false)
                .build();
        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);

        when(userDetails.getId()).thenReturn(data.getUserId());
        when(cardRepository.findById(data.getCardId())).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        ResponseEntity<?> result = cardService.requestBlockCard(data.getCardId(), userDetails);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Block card request is created", result.getBody());
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void requestBlockCard_forbiddenIfNotOwner() {
        User user = User.builder().id(data.getUserId()).build();
        Card card = Card.builder()
                .id(data.getCardId())
                .user(user)
                .isBlockRequested(false)
                .build();
        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);

        when(userDetails.getId()).thenReturn(data.getCurrentUserId());
        when(cardRepository.findById(data.getCardId())).thenReturn(Optional.of(card));

        ResponseEntity<?> result = cardService.requestBlockCard(data.getCardId(), userDetails);

        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void requestBlockCard_cardNotFound() {
        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);

        lenient().when(userDetails.getId()).thenReturn(data.getUserId());
        when(cardRepository.findById(data.getCardId())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> cardService.requestBlockCard(data.getCardId(), userDetails));
        verify(cardRepository, never()).save(any());
    }

    @Test
    void makeTransfer_success() {
        User user = User.builder().id(data.getUserId()).build();
        Card senderCard = Card.builder()
                .id(data.getSenderCardId())
                .user(user)
                .balance(data.getSuccessBalance())
                .dailyLimit(data.getSuccessDailyLimit())
                .build();
        Card receiverCard = Card.builder()
                .id(data.getReceiverCardId())
                .user(user)
                .balance(data.getSuccessBalance())
                .dailyLimit(data.getSuccessDailyLimit())
                .build();

        when(cardRepository.findById(data.getSenderCardId())).thenReturn(Optional.of(senderCard));
        when(cardRepository.findById(data.getReceiverCardId())).thenReturn(Optional.of(receiverCard));
        when(cardRepository.save(any(Card.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        ResponseEntity<?> result = cardService.makeTransfer(
                data.getSenderCardId(),
                data.getReceiverCardId(),
                data.getPayment(),
                data.getUserId()
        );

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Payment successful", result.getBody());
        assertEquals(data.getSuccessBalance().subtract(data.getPayment()), senderCard.getBalance());
        assertEquals(data.getSuccessBalance().add(data.getPayment()), receiverCard.getBalance());
        verify(cardRepository, times(2)).save(any());
    }

    @Test
    void makeTransfer_anyCardIsNotBelongToUser() {
        User user = User.builder().id(data.getUserId()).build();
        User otherUser = User.builder().id(data.getCurrentUserId()).build();
        Card senderCard = Card.builder()
                .id(data.getSenderCardId())
                .user(user)
                .balance(data.getSuccessBalance())
                .dailyLimit(data.getSuccessDailyLimit())
                .build();
        Card receiverCard = Card.builder()
                .id(data.getReceiverCardId())
                .user(otherUser)
                .balance(data.getSuccessBalance())
                .dailyLimit(data.getSuccessDailyLimit())
                .build();

        when(cardRepository.findById(data.getSenderCardId())).thenReturn(Optional.of(senderCard));
        when(cardRepository.findById(data.getReceiverCardId())).thenReturn(Optional.of(receiverCard));

        ResponseEntity<?> result = cardService.makeTransfer(
                data.getSenderCardId(),
                data.getReceiverCardId(),
                data.getPayment(),
                data.getUserId()
        );

        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
        assertEquals("Both cards do not belong to user with ID" + data.getUserId(), result.getBody());
        verify(cardRepository, never()).save(any());
    }

    @Test
    void makeTransfer_insufficientFunds() {
        User user = User.builder().id(data.getUserId()).build();
        Card senderCard = Card.builder()
                .id(data.getSenderCardId())
                .user(user)
                .balance(data.getFailedBalance())
                .dailyLimit(data.getSuccessDailyLimit())
                .build();
        Card receiverCard = Card.builder()
                .id(data.getReceiverCardId())
                .user(user)
                .balance(data.getSuccessBalance())
                .dailyLimit(data.getSuccessDailyLimit())
                .build();

        when(cardRepository.findById(data.getSenderCardId())).thenReturn(Optional.of(senderCard));
        when(cardRepository.findById(data.getReceiverCardId())).thenReturn(Optional.of(receiverCard));

        ResponseEntity<?> result = cardService.makeTransfer(
                data.getSenderCardId(),
                data.getReceiverCardId(),
                data.getPayment(),
                data.getUserId()
        );

        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
        assertEquals("Insufficient funds", result.getBody());
        verify(cardRepository, never()).save(any());
    }

    @Test
    void makeTransfer_dailyLimitExceeded() {
        User user = User.builder().id(data.getUserId()).build();
        Card senderCard = Card.builder()
                .id(data.getSenderCardId())
                .user(user)
                .balance(data.getSuccessBalance())
                .dailyLimit(data.getFailedDailyLimit())
                .build();
        Card receiverCard = Card.builder()
                .id(data.getReceiverCardId())
                .user(user)
                .balance(data.getSuccessBalance())
                .dailyLimit(data.getSuccessDailyLimit())
                .build();

        when(cardRepository.findById(data.getSenderCardId())).thenReturn(Optional.of(senderCard));
        when(cardRepository.findById(data.getReceiverCardId())).thenReturn(Optional.of(receiverCard));

        ResponseEntity<?> result = cardService.makeTransfer(
                data.getSenderCardId(),
                data.getReceiverCardId(),
                data.getPayment(),
                data.getUserId()
        );

        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
        assertEquals("Daily transfer limit spent", result.getBody());
        verify(cardRepository, never()).save(any());
    }

    @Test
    void makeTransfer_cardNotFound() {
        when(cardRepository.findById(data.getSenderCardId())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> cardService.makeTransfer(
                data.getSenderCardId(),
                data.getReceiverCardId(),
                data.getPayment(),
                data.getUserId()
        ));
    }

    @Test
    void getCards_getAll_success() {
        Pageable pageable = PageRequest.of(0, 10);

        List<Card> cards = List.of(
                Card.builder().id(data.getCardId()).build(),
                Card.builder().id(data.getCardId() + 1).build(),
                Card.builder().id(data.getCardId() + 2).build()
        );

        Page<Card> cardPage = new PageImpl<>(cards, pageable, cards.size());

        List<CardReadDto> dtos = List.of(
                CardReadDto.builder().id(data.getCardId()).build(),
                CardReadDto.builder().id(data.getCardId() + 1).build(),
                CardReadDto.builder().id(data.getCardId() + 2).build()
        );

        when(cardRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(cardPage);
        when(cardMapper.toDto(any(Card.class))).thenAnswer(invocation -> {
            Card card = invocation.getArgument(0);
            return dtos.stream().filter(d -> d.getId().equals(card.getId())).findFirst().orElse(null);
        });

        Page<CardReadDto> result = cardService.getCards(filter, pageable);

        assertNotNull(result);
        assertEquals(3, result.getContent().size());
        assertEquals(1L, result.getContent().get(0).getId());
        assertEquals(2L, result.getContent().get(1).getId());

        verify(cardRepository).findAll(any(Specification.class), eq(pageable));
        verify(cardMapper, times(3)).toDto(any(Card.class));
    }

    @Test
    void getCards_getByFilter_success() {
        filter.setUserId(data.getUserId());
        filter.setStatus(CardStatus.ACTIVE);
        Pageable pageable = PageRequest.of(0, 10);

        User user = User.builder().id(data.getUserId()).build();
        Card card = Card.builder().id(data.getCardId()).user(user).status(CardStatus.ACTIVE).build();
        Page<Card> cardPage = new PageImpl<>(List.of(card), pageable, 1);

        CardReadDto dto = CardReadDto.builder().id(data.getCardId()).build();

        when(cardRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(cardPage);
        when(cardMapper.toDto(any(Card.class))).thenReturn(dto);

        Page<CardReadDto> result = cardService.getCards(filter, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1L, result.getContent().get(0).getId());

        verify(cardRepository).findAll(any(Specification.class), eq(pageable));
        verify(cardMapper).toDto(card);
    }

    @Test
    void getCards_emptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Card> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(cardRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(emptyPage);

        Page<CardReadDto> result = cardService.getCards(filter, pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(cardRepository).findAll(any(Specification.class), eq(pageable));
        verify(cardMapper, never()).toDto(any(Card.class));
    }

    @Test
    void getUserCards_success() {
        User user = User.builder().id(data.getUserId()).build();
        List<Card> cards = List.of(
                Card.builder().id(data.getCardId()).user(user).build(),
                Card.builder().id(data.getCardId() + 1).user(user).build(),
                Card.builder().id(data.getCardId() + 2).user(user).build()
        );
        List<CardReadDto> dtos = List.of(
                CardReadDto.builder().id(data.getCardId()).build(),
                CardReadDto.builder().id(data.getCardId() + 1).build(),
                CardReadDto.builder().id(data.getCardId() + 2).build()
        );

        when(cardRepository.findByUserId(data.getUserId())).thenReturn(cards);
        when(cardMapper.toCardReadDtos(cards)).thenReturn(dtos);

        List<CardReadDto> result = cardService.getUserCards(data.getUserId());
        assertEquals(dtos, result);
        verify(cardMapper).toCardReadDtos(cards);
    }

    @Test
    void getUserCards_userHasNoCards() {
        User user = User.builder().id(data.getUserId()).build();
        List<Card> cards = Collections.emptyList();
        List<CardReadDto> dtos = Collections.emptyList();

        when(cardRepository.findByUserId(data.getUserId())).thenReturn(cards);
        when(cardMapper.toCardReadDtos(cards)).thenReturn(Collections.emptyList());

        List<CardReadDto> result = cardService.getUserCards(data.getUserId());
        assertEquals(dtos, result);
        verify(cardMapper).toCardReadDtos(cards);
    }

    @Test
    void findCardById_success() {
        Card card = Card.builder().id(data.getCardId()).build();

        when(cardRepository.findById(data.getCardId())).thenReturn(Optional.of(card));

        Card result = cardService.findCardById(data.getCardId());
        assertEquals(card, result);
    }

    @Test
    void findCardById_cardNotFound() {
        when(cardRepository.findById(data.getCardId())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> cardService.findCardById(data.getCardId()));
    }

    @Test
    void getCardById_success() {
        Card card = Card.builder().id(data.getCardId()).build();
        CardReadDto dto = CardReadDto.builder().id(data.getCardId()).build();

        when(cardRepository.findById(data.getCardId())).thenReturn(Optional.of(card));
        when(cardMapper.toDto(any(Card.class))).thenReturn(dto);

        CardReadDto result = cardService.getCardById(data.getCardId());
        assertEquals(dto, result);
    }

    @Test
    void getCardById_cardNotFound() {
        when(cardRepository.findById(data.getCardId())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> cardService.getCardById(data.getCardId()));
    }

    @Test
    void getCardTransactionList_success() {
        User user = User.builder().id(data.getUserId()).build();
        List<Transaction> transactions = List.of(
                Transaction.builder().id(data.getTransactionId()).build(),
                Transaction.builder().id(data.getTransactionId() + 1).build()
        );
        Card card = Card.builder()
                .id(data.getCardId())
                .user(user)
                .transactions(transactions)
                .build();
        List<TransactionDto> dtos = List.of(
                TransactionDto.builder().id(data.getTransactionId()).build(),
                TransactionDto.builder().id(data.getTransactionId() + 1).build()
        );

        when(cardRepository.findById(data.getCardId())).thenReturn(Optional.of(card));
        when(transactionMapper.toTransactionDtos(transactions)).thenReturn(dtos);

        List<TransactionDto> result = cardService.getCardTransactionList(data.getCardId(), data.getUserId());
        assertEquals(dtos, result);
        verify(transactionMapper).toTransactionDtos(transactions);
    }

    @Test
    void getCardTransactionList_emptyListIfNotOwner() {
        User user = User.builder().id(data.getCurrentUserId()).build();
        Card card = Card.builder()
                .id(data.getCardId())
                .user(user)
                .transactions(Collections.emptyList())
                .build();

        when(cardRepository.findById(data.getCardId())).thenReturn(Optional.of(card));

        List<TransactionDto> result = cardService.getCardTransactionList(data.getCardId(), data.getUserId());

        assertTrue(result.isEmpty());
        verify(transactionMapper, never()).toTransactionDtos(any());
    }

    @Test
    void getCardTransactionList_cardNotFound() {
        when(cardRepository.findById(data.getCardId())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> cardService.getCardTransactionList(
                data.getCardId(),
                data.getUserId()
        ));
    }
}
