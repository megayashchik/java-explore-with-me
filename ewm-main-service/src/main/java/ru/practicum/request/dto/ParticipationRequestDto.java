package ru.practicum.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.request.model.RequestStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipationRequestDto {
	Long id;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime created;

	Long event;

	Long requester;

	RequestStatus status;
}