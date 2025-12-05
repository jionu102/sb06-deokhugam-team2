package com.codeit.sb06deokhugamteam2.comment.repository;

import com.codeit.sb06deokhugamteam2.comment.entity.Comment;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface CommentQueryRepository {

    List<Comment> findCommentsByCursor(
            UUID reviewId,
            Instant after,
            UUID cursor,
            String direction,
            int size
    );

    long countByReviewId(UUID reviewId);
}
