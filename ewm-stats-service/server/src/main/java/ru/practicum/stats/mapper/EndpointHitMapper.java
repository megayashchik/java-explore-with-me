package ru.practicum.stats.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.EndpointHitRequest;
import ru.practicum.stats.model.EndpointHit;

@Mapper(componentModel = "spring")
public interface EndpointHitMapper {
	EndpointHitMapper INSTANCE = Mappers.getMapper(EndpointHitMapper.class);

	EndpointHit mapDtoToEntity(EndpointHitRequest hit);
}