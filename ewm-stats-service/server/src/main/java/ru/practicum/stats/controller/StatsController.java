package ru.practicum.stats.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EndpointHitRequest;
import ru.practicum.ViewStatsResponse;
import ru.practicum.stats.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class StatsController {
	private final StatsService statsService;

	@PostMapping("/hit")
	@ResponseStatus(HttpStatus.CREATED)
	public void hit(@RequestBody @Valid EndpointHitRequest hit) {
		statsService.saveHit(hit);
	}

	@GetMapping("/stats")
	public List<ViewStatsResponse> getStats(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
			@RequestParam(required = false) List<String> uris,
			@RequestParam(defaultValue = "false") Boolean unique) {
		return statsService.getStats(start, end, uris, unique);
	}
}