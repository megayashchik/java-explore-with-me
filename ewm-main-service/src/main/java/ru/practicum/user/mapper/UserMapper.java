package ru.practicum.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

	User toUser(NewUserRequest newUserRequest);

	UserDto toUserDto(User user);

	UserShortDto toUserShortDto(User user);

	List<UserDto> toUserDtoList(List<User> users);
}
