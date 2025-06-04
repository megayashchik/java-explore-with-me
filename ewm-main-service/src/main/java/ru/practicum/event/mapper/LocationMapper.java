package ru.practicum.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.practicum.event.dto.LocationDto;
import ru.practicum.event.model.Location;

@Mapper(
		unmappedTargetPolicy = ReportingPolicy.IGNORE,
		componentModel = MappingConstants.ComponentModel.SPRING
)
public interface LocationMapper {
	Location toEntity(LocationDto dto);

	LocationDto toDto(Location location);
}