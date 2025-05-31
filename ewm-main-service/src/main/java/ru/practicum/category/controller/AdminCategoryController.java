package ru.practicum.category.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {
	private final CategoryService categoryService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CategoryDto createCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {
		return categoryService.createCategory(newCategoryDto);
	}

	@DeleteMapping("/{catId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteCategory(@PathVariable Long catId) {
		categoryService.deleteCategory(catId);
	}

	@PatchMapping("/{catId}")
	public CategoryDto updateCategory(@PathVariable Long catId,
	                                  @Valid @RequestBody CategoryDto categoryDto) {
		return categoryService.updateCategory(catId, categoryDto);
	}
}
