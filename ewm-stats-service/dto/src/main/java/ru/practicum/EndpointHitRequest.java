package ru.practicum;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EndpointHitRequest {
	Long id;
	String app;
	String uri;
	String ip;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime timestamp;
}