package ru.practicum.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.Request;

@Mapper(componentModel = "spring")
public interface RequestMapper {

	@Mapping(target = "event", source = "event.id")
	@Mapping(target = "requester", source = "requester.id")
	@Mapping(target = "status", expression = "java(request.getStatus().toString())")
	ParticipationRequestDto toParticipationRequestDto(Request request);
}
