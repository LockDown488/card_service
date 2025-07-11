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
import ru.kopanev.spring.card_service.dto.user.UserEditDto;
import ru.kopanev.spring.card_service.dto.user.UserReadDto;
import ru.kopanev.spring.card_service.entity.User;
import ru.kopanev.spring.card_service.enums.Role;
import ru.kopanev.spring.card_service.mapper.UserMapper;
import ru.kopanev.spring.card_service.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final Long USER_ID = 1L;
    private static final Role USER_ROLE = Role.USER;
    private static final Role ADMIN_ROLE = Role.ADMIN;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void updateUser_success() {
        User user = User.builder()
                .id(USER_ID)
                .role(USER_ROLE)
                .build();
        UserEditDto editDto = UserEditDto.builder()
                .id(USER_ID)
                .role(ADMIN_ROLE)
                .build();
        User updatedUser = User.builder()
                .id(USER_ID)
                .role(ADMIN_ROLE)
                .build();
        UserReadDto dto = UserReadDto.builder()
                .id(USER_ID)
                .role(ADMIN_ROLE)
                .build();

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userMapper.update(editDto, user)).thenReturn(updatedUser);
        when(userRepository.save(updatedUser)).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        when(userMapper.toDto(updatedUser)).thenReturn(dto);

        UserReadDto result = userService.updateUser(editDto);
        assertEquals(dto, result);
        verify(userMapper).update(any(UserEditDto.class), any(User.class));
        verify(userRepository).save(any(User.class));
        verify(userMapper).toDto(any(User.class));
    }

    @Test
    void updateUser_userNotFound() {
        UserEditDto editDto = UserEditDto.builder()
                .id(USER_ID)
                .role(ADMIN_ROLE)
                .build();

        when(userRepository.findById(USER_ID)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> userService.updateUser(editDto));
    }

    @Test
    void deleteUser_success() {
        userService.deleteUser(USER_ID);

        verify(userRepository).deleteById(USER_ID);
    }

    @Test
    void findUserById_success() {
        User user = User.builder().id(USER_ID).build();

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        User result = userService.findUserById(USER_ID);
        assertEquals(user, result);
        verify(userRepository).findById(USER_ID);
    }

    @Test
    void findUserById_userNotFound() {
        when(userRepository.findById(USER_ID)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> userService.findUserById(USER_ID));
    }

    @Test
    void getUserById_success() {
        User user = User.builder().id(USER_ID).build();
        UserReadDto dto = UserReadDto.builder().id(USER_ID).build();

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userMapper.toDto(any(User.class))).thenReturn(dto);

        UserReadDto result = userService.getUserById(USER_ID);
        assertEquals(dto, result);
    }

    @Test
    void getUserById_userNotFound() {
        when(userRepository.findById(USER_ID)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(USER_ID));
    }

    @Test
    void getUsers_success() {
        Pageable pageable = PageRequest.of(0, 10);

        List<User> users = List.of(
                User.builder().id(USER_ID).build(),
                User.builder().id(USER_ID + 1).build(),
                User.builder().id(USER_ID + 2).build()
        );

        Page<User> userPage = new PageImpl<>(users, pageable, users.size());

        List<UserReadDto> dtos = List.of(
                UserReadDto.builder().id(USER_ID).build(),
                UserReadDto.builder().id(USER_ID + 1).build(),
                UserReadDto.builder().id(USER_ID + 2).build()
        );

        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userMapper.toDto(any(User.class))).thenAnswer(invocationOnMock -> {
            User user = invocationOnMock.getArgument(0);
            return dtos.stream().filter(d -> d.getId().equals(user.getId())).findFirst().orElse(null);
        });

        Page<UserReadDto> result = userService.getUsers(pageable);

        assertNotNull(result);
        assertEquals(3, result.getContent().size());
        assertEquals(1L, result.getContent().get(0).getId());
        assertEquals(2L, result.getContent().get(1).getId());
        assertEquals(3L, result.getContent().get(2).getId());

        verify(userRepository).findAll(pageable);
        verify(userMapper, times(3)).toDto(any(User.class));
    }

    @Test
    void getUsers_emptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(userRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<UserReadDto> result = userService.getUsers(pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository).findAll(pageable);
        verify(userMapper, never()).toDto(any(User.class));
    }

}
