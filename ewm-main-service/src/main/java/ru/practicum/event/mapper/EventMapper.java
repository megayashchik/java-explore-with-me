package ru.practicum.event.mapper;

import org.mapstruct.*;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.Event;
import ru.practicum.user.mapper.UserMapper;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class, UserMapper.class})
public interface EventMapper {

	@Mapping(target = "category", ignore = true)
	@Mapping(target = "initiator", ignore = true)
	@Mapping(target = "lat", source = "location.lat")
	@Mapping(target = "lon", source = "location.lon")
	Event toEvent(NewEventDto dto);

	@Mapping(target = "location.lat", source = "lat")
	@Mapping(target = "location.lon", source = "lon")
	EventFullDto toEventFullDto(Event event);

	EventShortDto toEventShortDto(Event event);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "category", ignore = true)
	@Mapping(target = "state", ignore = true)
	@Mapping(target = "lat", source = "location.lat")
	@Mapping(target = "lon", source = "location.lon")
	void updateEventFromUserRequest(UpdateEventUserRequest dto, @MappingTarget Event event);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "category", ignore = true)
	@Mapping(target = "state", ignore = true)
	@Mapping(target = "lat", source = "location.lat")
	@Mapping(target = "lon", source = "location.lon")
	void updateEventFromAdminRequest(UpdateEventAdminRequest dto, @MappingTarget Event event);
}
