package ru.kopanev.spring.card_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.kopanev.spring.card_service.entity.Card;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long>, JpaSpecificationExecutor<Card> {

    boolean existsByCardNumber(String cardNumber);

    Card findByCardNumber(String cardNumber);

    List<Card> findByUserId(Long userId);
}
