package ru.kopanev.spring.card_service.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.kopanev.spring.card_service.entity.Card;
import ru.kopanev.spring.card_service.filter.CardFilter;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

public class CardSpecification {

    public static Specification<Card> filterBy(CardFilter cardFilter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (cardFilter.getUserId() != null) {
                predicates.add(cb.equal(root.get("user").get("id"), cardFilter.getUserId()));
            }

            if (cardFilter.getExpiryDate() != null) {
                predicates.add(cb.equal(root.get("expiryDate"), cardFilter.getExpiryDate()));
            }

            if (cardFilter.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), cardFilter.getStatus()));
            }

            if (cardFilter.getDailyLimit() != null) {
                predicates.add(cb.equal(root.get("dailyLimit"), cardFilter.getDailyLimit()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
