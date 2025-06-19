package ru.kopanev.spring.card_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import ru.kopanev.spring.card_service.dto.user.UserReadDto;
import ru.kopanev.spring.card_service.entity.Card;
import ru.kopanev.spring.card_service.entity.User;


import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @Mapping(target = "cards", ignore = true)
    User toEntity(UserReadDto dto);

    @Mapping(source = "cards", target = "cardsIds", qualifiedByName = "mapToLong")
    UserReadDto toDto(User entity);

    @Named("mapToLong")
    default List<Long> mapToLong(List<Card> cards) {
        return cards.stream()
                .map(Card::getId)
                .toList();
    }

    default User toEntityWithCards(UserReadDto dto, List<Card> cards) {
        User user = toEntity(dto);
        user.setCards(cards);
        return user;
    }
}
