package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
	private final CategoryRepository categoryRepository;
	private final EventRepository eventRepository;
	private final CategoryMapper categoryMapper;

	public List<CategoryDto> getCategories(Integer from, Integer size) {
		PageRequest page = PageRequest.of(from / size, size);
		List<Category> categories = categoryRepository.findAll(page).getContent();
		return categoryMapper.toCategoryDtoList(categories);
	}

	public CategoryDto getCategory(Long catId) {
		Category category = categoryRepository.findById(catId)
				.orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));
		return categoryMapper.toCategoryDto(category);
	}

	@Transactional
	public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
		try {
			Category category = categoryMapper.toCategory(newCategoryDto);
			Category savedCategory = categoryRepository.save(category);
			return categoryMapper.toCategoryDto(savedCategory);
		} catch (Exception e) {
			throw new ConflictException("Category with name " + newCategoryDto.getName() + " already exists");
		}
	}

	@Transactional
	public void deleteCategory(Long catId) {
		if (!categoryRepository.existsById(catId)) {
			throw new NotFoundException("Category with id=" + catId + " was not found");
		}

		if (eventRepository.existsByCategoryId(catId)) {
			throw new ConflictException("The category is not empty");
		}

		categoryRepository.deleteById(catId);
	}

	@Transactional
	public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
		Category category = categoryRepository.findById(catId)
				.orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));

		if (!category.getName().equals(categoryDto.getName()) &&
				categoryRepository.existsByName(categoryDto.getName())) {
			throw new ConflictException("Category with name " + categoryDto.getName() + " already exists");
		}

		category.setName(categoryDto.getName());
		Category savedCategory = categoryRepository.save(category);
		return categoryMapper.toCategoryDto(savedCategory);
	}
}

