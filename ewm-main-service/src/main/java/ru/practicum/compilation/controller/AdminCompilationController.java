package ru.practicum.compilation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationDto;
import ru.practicum.compilation.service.CompilationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class AdminCompilationController {
	private final CompilationService compilationService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CompilationDto createCompilation(@Valid @RequestBody NewCompilationDto dto) {
		return compilationService.createCompilation(dto);
	}

	@PatchMapping("/{compId}")
	public CompilationDto updateCompilation(@PathVariable Long compId, @Valid @RequestBody UpdateCompilationDto dto) {
		return compilationService.updateCompilation(compId, dto);
	}

	@DeleteMapping("/{compId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteCompilation(@PathVariable Long compId) {
		compilationService.deleteCompilation(compId);
	}
}