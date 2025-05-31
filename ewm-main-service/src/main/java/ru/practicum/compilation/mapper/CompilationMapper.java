package ru.practicum.compilation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.mapper.EventMapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {EventMapper.class})
public interface CompilationMapper {

	@Mapping(target = "events", ignore = true)
	Compilation toCompilation(NewCompilationDto newCompilationDto);

	CompilationDto toCompilationDto(Compilation compilation);

	List<CompilationDto> toCompilationDtoList(List<Compilation> compilations);
}
