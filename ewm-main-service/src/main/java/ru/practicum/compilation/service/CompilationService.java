package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationDto;

import java.util.List;

public interface CompilationService {
	CompilationDto createCompilation(NewCompilationDto dto);

	CompilationDto updateCompilation(Long compId, UpdateCompilationDto dto);

	void deleteCompilation(Long compId);

	CompilationDto findCompilationById(Long compId);

	List<CompilationDto> findCompilations(Boolean pinned, Integer from, Integer size);
}