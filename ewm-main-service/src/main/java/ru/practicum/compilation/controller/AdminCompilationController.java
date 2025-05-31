package ru.practicum.compilation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
public class AdminCompilationController {
	private final CompilationService compilationService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CompilationDto createCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
		return compilationService.createCompilation(newCompilationDto);
	}

	@DeleteMapping("/{compId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteCompilation(@PathVariable Long compId) {
		compilationService.deleteCompilation(compId);
	}

	@PatchMapping("/{compId}")
	public CompilationDto updateCompilation(@PathVariable Long compId,
	                                        @Valid @RequestBody UpdateCompilationRequest updateRequest) {
		return compilationService.updateCompilation(compId, updateRequest);
	}
}