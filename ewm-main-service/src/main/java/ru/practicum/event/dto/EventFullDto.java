package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventFullDto {
	private Long id;
	private String annotation;
	private CategoryDto category;
	private Long confirmedRequests;
	private LocalDateTime createdOn;
	private String description;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime eventDate;
	private UserShortDto initiator;
	private LocationDto location;
	private Boolean paid;
	private Integer participantLimit;
	private LocalDateTime publishedOn;
	private Boolean requestModeration;
	private String state;
	private String title;
	private Long views;
}