package ru.kopanev.spring.card_service.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.kopanev.spring.card_service.dto.user.UserEditDto;
import ru.kopanev.spring.card_service.dto.user.UserReadDto;
import ru.kopanev.spring.card_service.entity.User;
import ru.kopanev.spring.card_service.mapper.UserMapper;
import ru.kopanev.spring.card_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserReadDto updateUser(UserEditDto userEditDto) {
        User user = findUserById(userEditDto.getId());
        User updatedUser = userRepository.save(userMapper.update(userEditDto, user));

        return userMapper.toDto(updatedUser);
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }

    public UserReadDto getUserById(Long userId) {
        return userMapper.toDto(findUserById(userId));
    }

    public Page<UserReadDto> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toDto);
    }
}
