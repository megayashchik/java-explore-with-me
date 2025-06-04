package ru.practicum.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;

@Mapper(
		unmappedTargetPolicy = ReportingPolicy.IGNORE,
		componentModel = MappingConstants.ComponentModel.SPRING
)
public interface UserMapper {
	UserDto toDto(User user);

	@Mapping(target = "id", ignore = true)
	User toEntity(UserDto dto);
}