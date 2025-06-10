package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.comment.dto.CommentDtoRequest;
import ru.practicum.comment.dto.CommentDtoResponse;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.comment.service.specification.DbCommentSpecification;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
	private final CommentRepository commentRepository;
	private final EventRepository eventRepository;
	private final UserRepository userRepository;
	private final CommentMapper commentMapper;

	@Override
	public CommentDtoResponse createComment(Long userId, Long eventId, CommentDtoRequest dto) {
		log.info("Создание комментария для события с id = {} пользователем с id = {}", eventId, userId);

		Event event = eventRepository.findById(eventId).orElseThrow(() ->
				new NotFoundException("Событие с id = " + eventId + " не найдено"));

		if (event.getState() != State.PUBLISHED) {
			throw new ValidationException("Событие с id = " + eventId + " не опубликовано");
		}

		User user = userRepository.findById(userId).orElseThrow(() ->
				new NotFoundException("Пользователь с id = " + userId + " не найден"));

		Comment comment = commentRepository.save(commentMapper.toEntity(dto, event, user, LocalDateTime.now()));
		log.info("Комментарий с id = {} успешно создан пользователем с id = {} для события с id = {}",
				comment.getId(), userId, eventId);

		return commentMapper.toDto(comment);
	}

	@Override
	public CommentDtoResponse updateComment(Long userId, Long eventId, Long commId, CommentDtoRequest dto) {
		log.info("Обновление комментария с id = {} для события с id = {} пользователем с id = {}",
				commId, eventId, userId);

		Comment comment = getCommentCheckParams(userId, eventId, commId);
		comment.setText(dto.getText());
		log.info("Комментарий с id = {} успешно обновлен", commId);

		return commentMapper.toDto(commentRepository.save(comment));
	}

	@Override
	public void deleteComment(Long userId, Long eventId, Long commId) {
		log.info("Удаление комментария с id = {} для события с id = {} пользователем с id = {}",
				commId, eventId, userId);

		getCommentCheckParams(userId, eventId, commId);
		commentRepository.deleteById(commId);

		log.info("Комментарий с id = {} успешно удален", commId);
	}

	@Override
	public void deleteCommentByAdmin(Long commId) {
		log.info("Административное удаление комментария с id = {}", commId);

		if (!commentRepository.existsById(commId)) {
			throw new NotFoundException("Комментарий с id = " + commId + " не существует");
		}
		commentRepository.deleteById(commId);
		log.info("Комментарий с id = {} успешно удален администратором", commId);
	}

	@Override
	public CommentDtoResponse findCommentById(Long commentId) {
		log.info("Поиск комментария по с id = {}", commentId);

		Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
				new NotFoundException("Комментарий с id = " + commentId + " не найден"));
		log.info("Комментарий с id = {} успешно найден", commentId);

		return commentMapper.toDto(comment);
	}

	@Override
	public List<CommentDtoResponse> findCommentsByUserId(Long userId) {
		log.info("Поиск комментариев пользователя с id = {}", userId);

		if (!userRepository.existsById(userId)) {
			throw new NotFoundException("Пользователь с id = " + userId + " не найден");
		}

		List<Comment> comments = commentRepository.findAllByUserId(userId);
		log.info("Найдено {} комментариев пользователя с id = {}", comments.size(), userId);

		return commentMapper.toDto(comments);
	}

	@Override
	public List<CommentDtoResponse> findCommentsByEventId(Long eventId) {
		log.info("Поиск комментариев для события с id = {}", eventId);

		if (!eventRepository.existsById(eventId)) {
			throw new NotFoundException("Событие с id = " + eventId + " не найдено");
		}

		List<Comment> comments = commentRepository.findAllByEventId(eventId);
		log.info("Найдено {} комментариев для события с id = {}", comments.size(), eventId);

		return commentMapper.toDto(comments);
	}

	@Override
	public List<CommentDtoResponse> findCommentsByAdmin(List<Integer> users, List<Integer> events,
	                                                    LocalDateTime rangeStart, LocalDateTime rangeEnd,
	                                                    Integer from, Integer size) {
		log.info("Административный поиск комментариев с параметрами: " +
						"users = {}, events = {}, rangeStart = {}, rangeEnd = {}, from = {}, size = {}",
				users, events, rangeStart, rangeEnd, from, size);

		Specification<Comment> spec = DbCommentSpecification.getSpecificationAdmin(users, events, rangeStart, rangeEnd);

		Pageable pageable = PageRequest.of(from / size, size);
		List<Comment> comments = commentRepository.findAll(spec, pageable).getContent();
		log.info("Административный поиск вернул {} комментариев", comments.size());

		return commentMapper.toDto(comments);
	}

	private Comment getCommentCheckParams(Long userId, Long eventId, Long commId) {
		if (!eventRepository.existsById(eventId)) {
			throw new NotFoundException("Событие с id = " + eventId + " не найдено");
		}

		if (!userRepository.existsById(userId)) {
			throw new NotFoundException("Пользователь с id = " + userId + " не найден");
		}

		Comment comment = commentRepository.findById(commId).orElseThrow(() ->
				new NotFoundException("Комментарий с id = " + commId + " не существует"));

		if (!Objects.equals(comment.getUser().getId(), userId)) {
			throw new ForbiddenException("Пользователь с id = " + userId + " не является создателем комментария");
		}

		return comment;
	}
}