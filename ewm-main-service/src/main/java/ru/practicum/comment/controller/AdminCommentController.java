package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDtoResponse;
import ru.practicum.comment.service.CommentService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/comments")
public class AdminCommentController {
	private final CommentService commentService;

	@GetMapping
	public List<CommentDtoResponse> findCommentsAdmin(
			@RequestParam(required = false) List<Integer> users,
			@RequestParam(required = false) List<Integer> events,
			@RequestParam(required = false)
			@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
			LocalDateTime rangeStart,
			@RequestParam(required = false)
			@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
			LocalDateTime rangeEnd,
			@RequestParam(defaultValue = "0") Integer from,
			@RequestParam(defaultValue = "10") Integer size) {
		return commentService.findCommentsByAdmin(users, events, rangeStart, rangeEnd, from, size);
	}

	@DeleteMapping("/{commentId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteCommentAdmin(@PathVariable Long commentId) {
		commentService.deleteCommentByAdmin(commentId);
	}
}