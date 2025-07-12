package ru.kopanev.spring.card_service.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kopanev.spring.card_service.dto.user.UserCreateDto;
import ru.kopanev.spring.card_service.dto.user.UserEditDto;
import ru.kopanev.spring.card_service.dto.user.UserReadDto;
import ru.kopanev.spring.card_service.entity.User;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final SimpleDtoMapper simpleDtoMapper;

    public User toEntity(UserCreateDto dto) {
        if ( dto == null ) {
            return null;
        }

        return User.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .role(dto.getRole())
                .build();
    }

    public UserReadDto toDto(User entity) {
        if ( entity == null ) {
            return null;
        }

        return UserReadDto.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .role(entity.getRole())
                .cards(simpleDtoMapper.toCardSimpleDto(entity.getCards()))
                .build();
    }

    public User update(UserEditDto source, User target) {
        if ( source == null ) {
            return target;
        }

        if ( source.getEmail() != null ) {
            target.setEmail(source.getEmail());
        }

        if ( source.getPassword() != null ) {
            target.setPassword(source.getPassword());
        }

        if ( source.getRole() != null ) {
            target.setRole(source.getRole());
        }

        return target;
    }
}
