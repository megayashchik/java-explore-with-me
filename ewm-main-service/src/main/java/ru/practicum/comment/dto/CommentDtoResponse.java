package ru.practicum.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDtoResponse {
	Long id;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime created;

	EventShortDto event;

	UserShortDto user;

	String text;
}