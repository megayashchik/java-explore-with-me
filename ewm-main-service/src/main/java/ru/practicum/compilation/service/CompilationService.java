package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationService {
	private final CompilationRepository compilationRepository;
	private final EventRepository eventRepository;
	private final CompilationMapper compilationMapper;

	public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
		PageRequest page = PageRequest.of(from / size, size);
		List<Compilation> compilations;

		if (pinned != null) {
			compilations = compilationRepository.findAllByPinned(pinned, page);
		} else {
			compilations = compilationRepository.findAll(page).getContent();
		}

		return compilationMapper.toCompilationDtoList(compilations);
	}

	public CompilationDto getCompilation(Long compId) {
		Compilation compilation = compilationRepository.findById(compId)
				.orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));
		return compilationMapper.toCompilationDto(compilation);
	}

	@Transactional
	public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
		Compilation compilation = compilationMapper.toCompilation(newCompilationDto);

		if (newCompilationDto.getEvents() != null && !newCompilationDto.getEvents().isEmpty()) {
			Set<Event> events = new HashSet<>(eventRepository.findAllById(newCompilationDto.getEvents()));
			compilation.setEvents(events);
		} else {
			compilation.setEvents(new HashSet<>());
		}

		if (newCompilationDto.getPinned() == null) {
			compilation.setPinned(false);
		}

		Compilation savedCompilation = compilationRepository.save(compilation);
		return compilationMapper.toCompilationDto(savedCompilation);
	}

	@Transactional
	public void deleteCompilation(Long compId) {
		if (!compilationRepository.existsById(compId)) {
			throw new NotFoundException("Compilation with id=" + compId + " was not found");
		}
		compilationRepository.deleteById(compId);
	}

	@Transactional
	public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateRequest) {
		Compilation compilation = compilationRepository.findById(compId)
				.orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));

		if (updateRequest.getEvents() != null) {
			if (updateRequest.getEvents().isEmpty()) {
				compilation.setEvents(new HashSet<>());
			} else {
				Set<Event> events = new HashSet<>(eventRepository.findAllById(updateRequest.getEvents()));
				compilation.setEvents(events);
			}
		}

		if (updateRequest.getPinned() != null) {
			compilation.setPinned(updateRequest.getPinned());
		}

		if (updateRequest.getTitle() != null) {
			compilation.setTitle(updateRequest.getTitle());
		}

		Compilation savedCompilation = compilationRepository.save(compilation);
		return compilationMapper.toCompilationDto(savedCompilation);
	}
}
