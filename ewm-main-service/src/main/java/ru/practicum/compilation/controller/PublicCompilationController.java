package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.service.CompilationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
public class PublicCompilationController {
	private final CompilationService compilationService;

	@GetMapping("/{compId}")
	public CompilationDto findCompilationById(@PathVariable Long compId) {
		return compilationService.findCompilationById(compId);
	}

	@GetMapping
	public List<CompilationDto> findCompilations(@RequestParam(defaultValue = "false") Boolean pinned,
	                                             @RequestParam(defaultValue = "0") Integer from,
	                                             @RequestParam(defaultValue = "10") Integer size) {
		return compilationService.findCompilations(pinned, from, size);
	}
}