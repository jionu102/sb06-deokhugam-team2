package com.codeit.sb06deokhugamteam2.review.domain;

import com.codeit.sb06deokhugamteam2.review.domain.exception.ReviewPermissionDeniedException;

import java.time.Instant;
import java.util.UUID;

public class ReviewDomain {

    private final UUID id;
    private final UUID bookId;
    private final UUID userId;
    private ReviewRating rating;
    private ReviewContent content;
    private int likeCount;
    private int commentCount;
    private final Instant createdAt;
    private Instant updatedAt;

    public ReviewDomain(
            UUID id,
            UUID bookId,
            UUID userId,
            ReviewRating rating,
            ReviewContent content,
            int likeCount,
            int commentCount,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.bookId = bookId;
        this.userId = userId;
        this.rating = rating;
        this.content = content;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ReviewDomain write(UUID bookId, UUID userId, ReviewRating rating, ReviewContent content) {
        UUID id = UUID.randomUUID();
        int likeCount = 0;
        int commentCount = 0;
        Instant createdAt = Instant.now();
        Instant updatedAt = createdAt;

        return new ReviewDomain(
                id,
                bookId,
                userId,
                rating,
                content,
                likeCount,
                commentCount,
                createdAt,
                updatedAt
        );
    }

    public static ReviewDomain loadSnapshot(Snapshot snapshot) {
        UUID id = snapshot.id();
        UUID bookId = snapshot.bookId();
        UUID userId = snapshot.userId();
        ReviewRating rating = snapshot.rating();
        ReviewContent content = snapshot.content();
        int likeCount = snapshot.likeCount();
        int commentCount = snapshot.commentCount();
        Instant createdAt = snapshot.createdAt();
        Instant updatedAt = snapshot.updatedAt();

        return new ReviewDomain(
                id,
                bookId,
                userId,
                rating,
                content,
                likeCount,
                commentCount,
                createdAt,
                updatedAt
        );
    }

    public Snapshot createSnapshot() {
        return new Snapshot(
                id,
                bookId,
                userId,
                rating,
                content,
                likeCount,
                commentCount,
                createdAt,
                updatedAt
        );
    }

    public ReviewDomain verifyOwner(UUID requestUserId) {
        if (!userId.equals(requestUserId)) {
            throw new ReviewPermissionDeniedException(requestUserId);
        }
        return this;
    }

    public ReviewDomain edit(ReviewRating rating, ReviewContent content) {
        this.rating = rating;
        this.content = content;
        this.updatedAt = Instant.now();
        return this;
    }

    public UUID id() {
        return id;
    }

    public UUID bookId() {
        return bookId;
    }

    public UUID userId() {
        return userId;
    }

    public ReviewRating rating() {
        return rating;
    }

    public record Snapshot(
            UUID id,
            UUID bookId,
            UUID userId,
            ReviewRating rating,
            ReviewContent content,
            int likeCount,
            int commentCount,
            Instant createdAt,
            Instant updatedAt
    ) {
    }
}
