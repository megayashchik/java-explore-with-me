package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationDto;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
	private final CompilationMapper compilationMapper;
	private final CompilationRepository compilationRepository;
	private final EventRepository eventRepository;

	@Override
	public CompilationDto createCompilation(NewCompilationDto dto) {
		log.info("Создание подборки: {}", dto.getTitle());

		if (dto.getEvents() == null) {
			dto.setEvents(new ArrayList<>());
		}

		List<Long> eventIds = dto.getEvents();
		List<Event> events = eventRepository.findAllById(eventIds);
		log.info("Найдено {} событий для добавления в подборку", events.size());

		Compilation compilation = compilationRepository.save(compilationMapper.toEntity(dto, events));
		log.info("Создана подборка с id = {}", compilation.getId());

		return compilationMapper.toDto(compilation);
	}

	@Override
	public CompilationDto updateCompilation(Long compId, UpdateCompilationDto dto) {
		log.info("Обновление подборки с id = {}", compId);

		Compilation compilation = findCompilationById(compId);

		if (dto.getEvents() == null) {
			dto.setEvents(new ArrayList<>());
		}

		compilation.setPinned(dto.getPinned());

		if (!dto.getEvents().isEmpty()) {
			List<Long> eventIds = dto.getEvents();
			List<Event> events = eventRepository.findAllById(eventIds);
			compilation.setEvents(events);
		}

		if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
			compilation.setTitle(dto.getTitle());
		}
		log.info("Подборка с id = {} успешно обновлена", compId);

		return compilationMapper.toDto(compilationRepository.save(compilation));
	}

	@Override
	public void deleteCompilation(Long compId) {
		log.info("Удаление подборки с id = {}", compId);
		compilationRepository.deleteById(compId);
	}

	@Override
	public CompilationDto findCompilationDtoById(Long compId) {
		log.info("Поиск подборки с id = {}", compId);

		Compilation compilation = findCompilationById(compId);
		log.info("Подборка с id = {} успешно найдена", compId);

		return compilationMapper.toDto(compilation);
	}

	@Override
	public List<CompilationDto> findCompilations(Boolean pinned, Integer from, Integer size) {
		log.info("Поиск подборок с параметрами: pinned = {}, from = {}, size = {}", pinned, from, size);

		Pageable pageable = PageRequest.of(from / size, size);
		List<Compilation> result = compilationRepository.findAllByPinned(pinned, pageable).getContent();
		log.info("Найдено {} подборок для запроса (pinned = {}, from = {}, size = {})",
				result.size(), pinned, from, size);

		return result.stream()
				.map(compilationMapper::toDto)
				.toList();
	}

	private Compilation findCompilationById(Long compId) {
		return compilationRepository.findById(compId).orElseThrow(() ->
				new NotFoundException("Подборка с id = " + compId + " не найдена"));
	}
}