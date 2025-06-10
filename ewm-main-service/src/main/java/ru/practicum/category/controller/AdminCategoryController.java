package ru.practicum.category.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.service.CategoryService;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/categories")
public class AdminCategoryController {
	private final CategoryService categoryService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CategoryDto createCategory(@Valid @RequestBody NewCategoryDto dto) {
		return categoryService.createCategory(dto);
	}

	@PatchMapping("/{categoryId}")
	@ResponseStatus(HttpStatus.OK)
	public CategoryDto updateCategory(@Valid @RequestBody NewCategoryDto dto,
	                                  @PathVariable Long categoryId) {
		return categoryService.updateCategory(dto, categoryId);
	}

	@DeleteMapping("/{categoryId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteCategory(@PathVariable Long categoryId) {
		categoryService.deleteCategory(categoryId);
	}
}