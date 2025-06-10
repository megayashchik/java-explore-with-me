package ru.practicum.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDtoRequest;
import ru.practicum.comment.dto.CommentDtoResponse;
import ru.practicum.comment.service.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events/{eventId}/comments")
public class PrivateCommentController {
	private final CommentService commentService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CommentDtoResponse create(
			@PathVariable Long userId,
			@PathVariable Long eventId,
			@Valid @RequestBody CommentDtoRequest dto) {
		return commentService.createComment(userId, eventId, dto);
	}

	@PatchMapping("/{commId}")
	public CommentDtoResponse update(
			@PathVariable Long userId,
			@PathVariable Long eventId,
			@PathVariable Long commId,
			@Valid @RequestBody CommentDtoRequest dto) {
		return commentService.updateComment(userId, eventId, commId, dto);
	}

	@DeleteMapping("/{commId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(
			@PathVariable Long userId,
			@PathVariable Long eventId,
			@PathVariable Long commId) {
		commentService.deleteComment(userId, eventId, commId);
	}

	@GetMapping
	public List<CommentDtoResponse> findCommentsByUserId(@PathVariable Long userId) {
		return commentService.findCommentsByUserId(userId);
	}
}