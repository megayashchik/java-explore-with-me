package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConstraintViolationException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ResourceAlreadyExistsException;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
	private final EventRepository eventRepository;
	private final CategoryRepository categoryRepository;
	private final CategoryMapper categoryMapper;

	@Override
	public CategoryDto createCategory(NewCategoryDto dto) {
		log.info("Добавление категории: {}", dto.getName());

		if (categoryRepository.existsByName(dto.getName())) {
			throw new ResourceAlreadyExistsException("Название категории уже существует");
		}

		Category category = categoryRepository.save(categoryMapper.toEntity(dto));
		log.info("Категория добавлена: {}", dto.getName());

		return categoryMapper.toDto(category);
	}

	@Override
	public CategoryDto updateCategory(NewCategoryDto dto, Long categoryId) {
		log.info("Обновление категории с id = : {}, {}", categoryId, dto.getName());

		checkExist(categoryId);

		Optional<Category> existingCategory = categoryRepository.findByName(dto.getName());
		if (existingCategory.isPresent() && !existingCategory.get().getId().equals(categoryId)) {
			throw new ResourceAlreadyExistsException("Название категории уже существует");
		}

		Category category = new Category(categoryId, dto.getName());
		Category result = categoryRepository.save(category);
		log.info("Категория обновлена: {}", dto.getName());

		return categoryMapper.toDto(result);
	}

	@Override
	public void deleteCategory(Long categoryId) {
		log.info("Удаление категории с id = : {}", categoryId);

		if (eventRepository.existsByCategoryId(categoryId)) {
			throw new ConstraintViolationException("Категория не может быть удалена, " +
					"пока существуют связанные с ней события");
		}

		categoryRepository.deleteById(categoryId);
	}

	@Override
	public CategoryDto findCategoryById(Long categoryId) {
		log.info("Получение категории с id = : {}", categoryId);

		Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
				new NotFoundException("Категория с id = " + categoryId + " не найдена"));
		log.info("Получена категория с id = : {}", categoryId);

		return categoryMapper.toDto(category);
	}

	@Override
	public List<CategoryDto> findCategories(Integer from, Integer size) {
		log.info("Запрос на получение списка категорий: страница (from) = {}, размер страницы (size) = {}", from, size);

		Pageable pageable = PageRequest.of(from / size, size);
		List<Category> categories = categoryRepository.findAll(pageable).getContent();
		log.info("Найдено {} категорий для запроса (from = {}, size = {})", categories.size(), from, size);

		return categories.stream()
				.map(categoryMapper::toDto)
				.toList();
	}

	private void checkExist(Long categoryId) {
		if (!categoryRepository.existsById(categoryId)) {
			throw new NotFoundException("Категория с id = " + categoryId + " не найдена");
		}
	}
}