package com.codeit.sb06deokhugamteam2.comment.service;

import com.codeit.sb06deokhugamteam2.comment.entity.Comment;
import com.codeit.sb06deokhugamteam2.comment.dto.CommentCreateRequest;
import com.codeit.sb06deokhugamteam2.comment.dto.CommentDto;
import com.codeit.sb06deokhugamteam2.comment.mapper.CommentMapper;
import com.codeit.sb06deokhugamteam2.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final  CommentMapper commentMapper;


    public CommentDto create(CommentCreateRequest request) {

        log.info("start CommentService.create(): userId = {},reviewId = {}",request.userId(),request.reviewId());

        UUID userId = UUID.fromString(request.userId());
        UUID reviewId = UUID.fromString(request.reviewId());


        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with id {}", userId);
                    return new RuntimeException("User not found : " + userId);
                });
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> {
                    log.warn("Review not found with id {}", reviewId);
                    return new RuntimeException("Review not found : " + reviewId);
                });

        Comment comment = Comment.builder()
                .user(user)
                .review(review)
                .content(request.content())
                .build();

        Comment savedComment = commentRepository.save(comment);

        log.info("Comment saved with id: {}", savedComment.getId());

        return commentMapper.toDto(savedComment);

    }
}
