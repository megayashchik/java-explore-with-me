package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
	CategoryDto createCategory(NewCategoryDto dto);

	CategoryDto updateCategory(NewCategoryDto dto, Long categoryId);

	void deleteCategory(Long categoryId);

	CategoryDto findCategoryById(Long id);

	List<CategoryDto> findCategories(Integer from, Integer size);
}