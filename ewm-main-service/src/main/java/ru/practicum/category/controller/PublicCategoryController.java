package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/categories")
public class PublicCategoryController {
	private final CategoryService categoryService;

	@GetMapping
	public List<CategoryDto> findCategories(@RequestParam(defaultValue = "0") Integer from,
	                                        @RequestParam(defaultValue = "10") Integer size) {
		return categoryService.findCategories(from, size);
	}

	@GetMapping("/{categoryId}")
	public CategoryDto findCategoryById(@PathVariable Long categoryId) {
		return categoryService.findCategoryById(categoryId);
	}
}