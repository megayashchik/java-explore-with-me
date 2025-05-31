package ru.practicum.category.mapper;

import org.mapstruct.Mapper;
import ru.practicum.category.model.Category;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

	Category toCategory(NewCategoryDto newCategoryDto);

	CategoryDto toCategoryDto(Category category);

	List<CategoryDto> toCategoryDtoList(List<Category> categories);
}
