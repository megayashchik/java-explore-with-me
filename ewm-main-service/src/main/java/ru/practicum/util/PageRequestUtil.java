package ru.practicum.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PageRequestUtil {

	public static PageRequest of(Integer from, Integer size) {
		return PageRequest.of(from / size, size);
	}

	public static PageRequest of(Integer from, Integer size, Sort sort) {
		return PageRequest.of(from / size, size, sort);
	}
}