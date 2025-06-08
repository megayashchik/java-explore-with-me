package ru.practicum.event.mapper;

import org.mapstruct.*;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.rating.service.RatingService;
import ru.practicum.user.model.User;

import java.util.Collections;
import java.util.List;

@Mapper(
		unmappedTargetPolicy = ReportingPolicy.IGNORE,
		componentModel = MappingConstants.ComponentModel.SPRING
)
public interface EventMapper {
	@Mapping(target = "likes", ignore = true)
	@Mapping(target = "dislikes", ignore = true)
	EventFullDto toFullDto(Event event);

	@Mapping(target = "likes", ignore = true)
	@Mapping(target = "dislikes", ignore = true)
	EventShortDto toShortDto(Event event);

	List<EventShortDto> toShortDto(List<Event> events);

	default List<EventShortDto> toShortDtoWithRatings(List<Event> events, @Context RatingService ratingService) {
		if (events == null) {
			return Collections.emptyList();
		}

		return events.stream()
				.map(event -> {
					EventShortDto dto = toShortDto(event);
					addRatingToShort(dto, event, ratingService);
					return dto;
				})
				.toList();
	}

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "category", source = "categoryEntity")
	@Mapping(target = "initiator", source = "initiator")
	@Mapping(target = "eventDate", source = "dto.eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
	Event toEntity(NewEventDto dto, Category categoryEntity, User initiator);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "eventDate", source = "dto.eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
	Event toEntity(EventFullDto dto);

	@AfterMapping
	default void addRatingToFull(@MappingTarget EventFullDto dto,
	                             Event event,
	                             @Context RatingService ratingService) {
		if (event.getId() != null) {
			dto.setLikes(ratingService.countLikes(event.getId()));
			dto.setDislikes(ratingService.countDislikes(event.getId()));
		}
	}

	@AfterMapping
	default void addRatingToShort(@MappingTarget EventShortDto dto,
	                              Event event,
	                              @Context RatingService ratingService) {
		if (event.getId() != null) {
			dto.setLikes(ratingService.countLikes(event.getId()));
			dto.setDislikes(ratingService.countDislikes(event.getId()));
		}
	}
}