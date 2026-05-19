package ee.bcs.valitalgud.service;

import ee.bcs.valitalgud.controller.comment.dto.CommentResponseDto;
import ee.bcs.valitalgud.controller.comment.dto.CreateCommentDto;
import ee.bcs.valitalgud.infrastructure.error.ErrorResponse;
import ee.bcs.valitalgud.infrastructure.exception.BadRequestException;
import ee.bcs.valitalgud.infrastructure.exception.UnauthorizedException;
import ee.bcs.valitalgud.persistence.comment.Comment;
import ee.bcs.valitalgud.persistence.comment.CommentRepository;
import ee.bcs.valitalgud.persistence.contact.ContactRepository;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private static final int MAX_COMMENT_LENGTH = 1000;

    private final CommentRepository commentRepository;
    private final ContactRepository contactRepository;
    private final EventService eventService;

    @Transactional(readOnly = true)
    public List<CommentResponseDto> getComments(Integer eventId, Integer userId) {
        validateUserId(userId);
        eventService.getValidEventBy(eventId);
        return commentRepository.findByEventIdOrderByCreatedAtDesc(eventId)
                .stream()
                .map(this::toCommentResponseDto)
                .toList();
    }

    @Transactional
    public CommentResponseDto addComment(Integer eventId, Integer userId, CreateCommentDto createCommentDto) {
        validateUserId(userId);
        validateContent(createCommentDto.getContent());
        eventService.getValidEventBy(eventId);

        Comment comment = new Comment();
        comment.setEventId(eventId);
        comment.setUserId(userId);
        comment.setContent(createCommentDto.getContent().trim());
        Comment saved = commentRepository.save(comment);

        Comment withTimestamp = commentRepository.findById(saved.getId()).orElseThrow();
        return toCommentResponseDto(withTimestamp);
    }

    private CommentResponseDto toCommentResponseDto(Comment comment) {
        CommentResponseDto dto = new CommentResponseDto();
        dto.setCommentId(comment.getId());
        dto.setAuthorId(comment.getUserId());
        dto.setContent(comment.getContent());
        if (comment.getCreatedAt() != null) {
            dto.setCreatedAt(LocalDateTime.ofInstant(comment.getCreatedAt(), ZoneOffset.UTC));
        }
        contactRepository.findByUserId(comment.getUserId())
                .ifPresent(contact -> dto.setAuthorName(contact.getFullName()));
        return dto;
    }

    private void validateUserId(Integer userId) {
        if (userId == null) {
            throw new UnauthorizedException(ErrorResponse.NOT_AUTHENTICATED);
        }
    }

    private void validateContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new BadRequestException(ErrorResponse.COMMENT_CONTENT_REQUIRED);
        }
        if (content.trim().length() > MAX_COMMENT_LENGTH) {
            throw new BadRequestException(ErrorResponse.COMMENT_TOO_LONG);
        }
    }
}
