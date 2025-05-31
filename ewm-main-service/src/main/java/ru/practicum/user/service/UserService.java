package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
	private final UserRepository userRepository;
	private final UserMapper userMapper;

	public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
		PageRequest page = PageRequest.of(from / size, size);
		List<User> users;

		if (ids != null && !ids.isEmpty()) {
			users = userRepository.findAllByIdIn(ids, page);
		} else {
			users = userRepository.findAll(page).getContent();
		}

		return userMapper.toUserDtoList(users);
	}

	@Transactional
	public UserDto createUser(NewUserRequest newUserRequest) {
		try {
			User user = userMapper.toUser(newUserRequest);
			User savedUser = userRepository.save(user);
			return userMapper.toUserDto(savedUser);
		} catch (Exception e) {
			throw new ConflictException("User with email " + newUserRequest.getEmail() + " already exists");
		}
	}

	@Transactional
	public void deleteUser(Long userId) {
		if (!userRepository.existsById(userId)) {
			throw new NotFoundException("User with id=" + userId + " was not found");
		}
		userRepository.deleteById(userId);
	}
}
