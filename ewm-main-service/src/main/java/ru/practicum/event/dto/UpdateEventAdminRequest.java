package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.model.StateAction;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventAdminRequest {
	@Size(min = 20, max = 2000)
	String annotation;

	Long category;

	@Size(min = 20, max = 7000)
	String description;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	String eventDate;

	LocationDto location;

	Boolean paid;

	@PositiveOrZero
	Integer participantLimit;

	Boolean requestModeration;

	StateAction stateAction;

	@Size(min = 3, max = 120)
	String title;
}