package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.EndpointHitRequest;
import ru.practicum.ViewStatsResponse;
import ru.practicum.stats.mapper.EndpointHitMapper;
import ru.practicum.stats.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StatsService {
	private final StatsRepository statsRepository;
	private final EndpointHitMapper endpointHitMapper;

	public void saveHit(EndpointHitRequest hit) {
		log.debug("Сохранение информации о просмотре: {}", hit);
		statsRepository.saveAndFlush(endpointHitMapper.mapDtoToEntity(hit));
	}

	@Transactional(readOnly = true)
	public List<ViewStatsResponse> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
		log.debug("Получение статистики: start = {}, end = {}, uris = {}, unique = {}", start, end, uris, unique);

		if (start.isAfter(end)) {
			log.error("Неверный временной диапазон: начальная дата позже конечной");

			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Неверный временной диапазон: начальная дата должна быть раньше конечной");
		}
		try {
			if (unique) {
				if (uris != null && !uris.isEmpty()) {
					log.debug("Получение уникальных посещений для указанных URI");

					return statsRepository.findUniqueIpHitsForSpecifiedUris(start, end, uris);
				}
				log.debug("Получение уникальных посещений для всех URI");

				return statsRepository.findUniqueIpHitsForAllUris(start, end);
			} else {
				if (uris != null && !uris.isEmpty()) {
					log.debug("Получение всех посещений для указанных URI");

					return statsRepository.findTotalHitsForSpecifiedUris(start, end, uris);
				}
				log.debug("Получение всех посещений для всех URI");

				return statsRepository.findTotalHitsForAllUris(start, end);
			}
		} catch (Exception e) {
			log.error("Ошибка при получении статистики: {}", e.getMessage(), e);

			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Произошла ошибка при получении статистики");
		}
	}
}