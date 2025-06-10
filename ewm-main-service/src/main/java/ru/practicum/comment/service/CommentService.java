package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentDtoRequest;
import ru.practicum.comment.dto.CommentDtoResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentService {
	CommentDtoResponse createComment(Long userId, Long eventId, CommentDtoRequest dto);

	CommentDtoResponse updateComment(Long userId, Long eventId, Long commId, CommentDtoRequest dto);

	void deleteComment(Long userId, Long eventId, Long commId);

	void deleteCommentByAdmin(Long commId);

	CommentDtoResponse findCommentById(Long commentId);

	List<CommentDtoResponse> findCommentsByUserId(Long userId);

	List<CommentDtoResponse> findCommentsByEventId(Long eventId);

	List<CommentDtoResponse> findCommentsByAdmin(List<Integer> users, List<Integer> events, LocalDateTime rangeStart,
	                                             LocalDateTime rangeEnd, Integer from, Integer size);
}