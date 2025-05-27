package ru.practicum.stats.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.EndpointHitRequest;
import ru.practicum.stats.model.EndpointHit;

@UtilityClass
public class EndpointHitMapper {
	public EndpointHit mapDtoToEntity(EndpointHitRequest hit) {
		return new EndpointHit(
				hit.getId(),
				hit.getApp(),
				hit.getUri(),
				hit.getIp(),
				hit.getTimestamp()
		);
	}
}