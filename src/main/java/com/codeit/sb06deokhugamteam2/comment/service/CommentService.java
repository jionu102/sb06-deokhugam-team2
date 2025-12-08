package com.codeit.sb06deokhugamteam2.comment.service;

import com.codeit.sb06deokhugamteam2.comment.dto.CommentUpdateRequest;
import com.codeit.sb06deokhugamteam2.comment.dto.CursorPageResponseCommentDto;
import com.codeit.sb06deokhugamteam2.comment.entity.Comment;
import com.codeit.sb06deokhugamteam2.comment.dto.CommentCreateRequest;
import com.codeit.sb06deokhugamteam2.comment.dto.CommentDto;
import com.codeit.sb06deokhugamteam2.comment.mapper.CommentMapper;
import com.codeit.sb06deokhugamteam2.comment.repository.CommentRepository;
import com.codeit.sb06deokhugamteam2.common.exception.ErrorCode;
import com.codeit.sb06deokhugamteam2.common.exception.exceptions.CommentException;
import com.codeit.sb06deokhugamteam2.notification.NotificationComponent;
import com.codeit.sb06deokhugamteam2.notification.entity.dto.request.NotificationCreateRequest;
import com.codeit.sb06deokhugamteam2.review.adapter.out.entity.Review;
import com.codeit.sb06deokhugamteam2.user.entity.User;
import com.codeit.sb06deokhugamteam2.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final  CommentMapper commentMapper;
    private final NotificationComponent notificationComponent;

    @PersistenceContext
    private EntityManager em;


    public CommentDto create(CommentCreateRequest request) {

        log.info("start CommentService.create(): userId = {},reviewId = {}",request.userId(),request.reviewId());

        UUID userId = UUID.fromString(request.userId());
        UUID reviewId = UUID.fromString(request.reviewId());


        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommentException(ErrorCode.USER_NOT_FOUND, Map.of("userId", userId), HttpStatus.NOT_FOUND));

        Review review = em.getReference(Review.class, reviewId);


        Comment comment = Comment.builder()
                .user(user)
                .review(review)
                .content(request.content())
                .build();

        Comment savedComment = commentRepository.save(comment);

        String commenterNickname = user.getNickname();

        String notificationContent =
                "[" + commenterNickname + "]님이 나의 리뷰에 댓글을 남겼습니다.\n" +
                        request.content();

        NotificationCreateRequest req = new NotificationCreateRequest(
                review.user().getId(),
                reviewId,
                review.content(),
                notificationContent
        );

        notificationComponent.saveNotification(req);

        return commentMapper.toDto(savedComment);
    }

    public CommentDto update(UUID commentId, UUID userId, CommentUpdateRequest request) {

        log.info("updating comment : commentId = {},userId = {}",commentId,userId);

        Comment foundComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(ErrorCode.INVALID_DATA, Map.of("commentId", commentId), HttpStatus.NOT_FOUND));

        if (!foundComment.getUser().getId().equals(userId)) {
            throw new CommentException(ErrorCode.INVALID_USER_DATA, Map.of("userId", userId), HttpStatus.FORBIDDEN);
        }

        foundComment.updateComment(request.content());

        Comment updatedComment = commentRepository.save(foundComment);

        log.info("Comment updated with id: {}", updatedComment.getId());

        return commentMapper.toDto(updatedComment);
    }

    public CommentDto readComment(UUID commentId) {
        log.info("reading comment : commentId = {}", commentId);

        Comment foundComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(ErrorCode.INVALID_DATA, Map.of("commentId", commentId), HttpStatus.NOT_FOUND));


        return commentMapper.toDto(foundComment);
    }

    @Transactional(readOnly = true)
    public CursorPageResponseCommentDto readComments(
            String reviewIdStr,
            String directionStr,
            String cursorStr,
            String afterStr,
            int size
    ) {
        log.info("readComments: reviewId={}, direction={}, cursor={}, after={}, size={}",
                reviewIdStr, directionStr, cursorStr, afterStr, size);


        UUID reviewId = UUID.fromString(reviewIdStr);

        Review review = em.find(Review.class, reviewId);
        if (review == null) {
            throw new CommentException(
                    ErrorCode.INVALID_DATA,
                    Map.of("reviewId", reviewId),
                    HttpStatus.NOT_FOUND
            );
        }

        String direction = (directionStr == null) ? "DESC" : directionStr.toUpperCase();
        if (!direction.equals("ASC") && !direction.equals("DESC")) {
            direction = "DESC";
        }


        UUID cursor = parseUUID(cursorStr);
        Instant after = parseInstant(afterStr);


        List<Comment> comments = commentRepository.findCommentsByCursor(
                reviewId,
                after,
                cursor,
                direction,
                size
        );


        List<CommentDto> dtos = comments.stream()
                .map(commentMapper::toDto)
                .toList();


        long total = commentRepository.countByReviewId(reviewId);


        String nextCursor = comments.isEmpty() ? null :
                comments.get(comments.size() - 1).getId().toString();


        Instant nextAfter = comments.isEmpty() ? null :
                comments.get(comments.size() - 1).getCreatedAt();


        boolean hasNext = !comments.isEmpty() && comments.size() == size;


        return new CursorPageResponseCommentDto(
                dtos,
                nextCursor,
                nextAfter,
                dtos.size(),
                total,
                hasNext
        );
    }

    public void softDelete(UUID commentId, UUID userId) {
        log.info("softDelete comment: commentId={}, userId={}", commentId, userId);

        Comment foundComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(ErrorCode.INVALID_DATA, Map.of("commentId", commentId), HttpStatus.NOT_FOUND));

        if (!foundComment.getUser().getId().equals(userId)) {
            throw new CommentException(
                    ErrorCode.INVALID_USER_DATA,
                    Map.of("commentId", commentId),
                    HttpStatus.FORBIDDEN
            );
        }

        commentRepository.delete(foundComment);
    }

    public void hardDelete(UUID commentId, UUID userId) {
        log.info("hardDelete comment: commentId={}, userId={}", commentId, userId);

        Comment foundComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(ErrorCode.INVALID_DATA, Map.of("commentId", commentId), HttpStatus.NOT_FOUND));

        if (!foundComment.getUser().getId().equals(userId)) {
            throw new CommentException(
                    ErrorCode.INVALID_USER_DATA,
                    Map.of("commentId", commentId),
                    HttpStatus.FORBIDDEN
            );
        }

        commentRepository.hardDeleteById(commentId);
    }



    private UUID parseUUID(String raw) {
        if (raw == null) return null;
        try { return UUID.fromString(raw); }
        catch (Exception e) { return null; }
    }

    private Instant parseInstant(String raw) {
        if (raw == null) return null;
        try { return Instant.parse(raw); }
        catch (Exception e) { return null; }
    }

}
