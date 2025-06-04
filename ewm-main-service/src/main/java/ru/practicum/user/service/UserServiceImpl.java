package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.exception.ResourceAlreadyExistsException;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final UserMapper userMapper;

	public UserDto createUser(UserDto dto) {
		log.info("Создание пользователя : {}", dto.getName());

		if (userRepository.existsByEmail(dto.getEmail())) {
			throw new ResourceAlreadyExistsException("Пользователь с указанной почтой уже существует");
		}

		User user = userRepository.save(userMapper.toEntity(dto));
		log.info("Пользователь с id = {} успешно создан", user.getId());

		return userMapper.toDto(user);
	}

	public void deleteUser(Long userId) {
		log.info("Удаление пользователя с id = {}", userId);
		userRepository.deleteById(userId);
		log.info("Пользователь с id = {} успешно удалён", userId);
	}

	public List<UserDto> findUsers(List<Integer> ids, Integer from, Integer size) {
		log.info("Поиск пользователей: ids = {}, from = {}, size = {}", ids, from, size);

		Pageable pageable = PageRequest.of(from / size, size);
		List<User> users;

		if (ids == null || ids.isEmpty()) {
			users = userRepository.findAll(pageable).getContent();
		} else {
			users = userRepository.findByIdIn(ids, pageable).getContent();
		}

		log.info("Найдено {} пользователей по запросу (ids = {}, from = {}, size = {})", users.size(), ids, from, size);

		return users.stream()
				.map(userMapper::toDto)
				.toList();
	}
}