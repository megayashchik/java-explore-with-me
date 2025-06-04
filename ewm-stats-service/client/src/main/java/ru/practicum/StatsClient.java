package ru.practicum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.exception.InternalErrorException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatsClient {
	private final RestTemplate restTemplate;
	private final String serverUrl;

	public StatsClient(@Value("${client.url}") String serverUrl, RestTemplate rest) {
		this.restTemplate = rest;
		this.serverUrl = serverUrl;
	}

	public List<ViewStatsResponse> findStats(LocalDateTime start, LocalDateTime end,
	                                         List<String> uris, Boolean unique) {
		String uri = UriComponentsBuilder.fromHttpUrl(serverUrl)
				.path("/stats")
				.queryParam("start", start)
				.queryParam("end", end)
				.queryParam("uris", uris)
				.queryParam("unique", unique)
				.toUriString();

		try {
			ResponseEntity<List<ViewStatsResponse>> response = restTemplate.exchange(
					uri,
					HttpMethod.GET,
					null,
					new ParameterizedTypeReference<>() {
					}
			);

			return response.getBody();
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode().value() == 404) {
				throw new NotFoundException("Статистика не найдена");
			} else if (e.getStatusCode().value() == 400) {
				throw new ValidationException("Некорректные параметры запроса статистики");
			} else if (e.getStatusCode().is5xxServerError()) {
				throw new InternalErrorException("Ошибка сервера при получении статистики");
			}
			throw new RuntimeException("Неизвестная ошибка при получении статистики: " + e.getMessage());
		}
	}

	public void hit(EndpointHitRequest dto) {
		String uri = UriComponentsBuilder.fromHttpUrl(serverUrl)
				.path("/hit")
				.toUriString();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<EndpointHitRequest> entity = new HttpEntity<>(dto, headers);

		try {
			restTemplate.exchange(uri, HttpMethod.POST, entity, Void.class);
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode().value() == 400) {
				throw new ValidationException("Некорректные данные для сохранения запроса");
			} else if (e.getStatusCode().value() == 404) {
				throw new NotFoundException("Эндпоинт /hit не найден");
			} else if (e.getStatusCode().is5xxServerError()) {
				throw new InternalErrorException("Ошибка сервера при сохранении запроса");
			}
			throw new RuntimeException("Неизвестная ошибка при сохранении запроса: " + e.getMessage());
		}
	}
}