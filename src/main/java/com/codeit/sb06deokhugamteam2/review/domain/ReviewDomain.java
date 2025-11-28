package com.codeit.sb06deokhugamteam2.review.domain;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ReviewDomain {

    private final UUID id;
    private final UUID bookId;
    private final UUID userId;
    private boolean deleted;
    private int rating;
    private String content;
    private Set<ReviewLikeDomain> likes;
    private int commentCount;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ReviewDomain(ReviewCreationCommand command) {
        this.id = UUID.randomUUID();
        this.bookId = command.bookId();
        this.userId = command.userId();
        this.deleted = false;
        this.rating = command.rating();
        this.content = command.content();
        this.likes = new HashSet<>();
        this.commentCount = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public Snapshot createSnapshot() {
        return new Snapshot(
                id,
                deleted,
                rating,
                content,
                Set.copyOf(likes),
                commentCount,
                createdAt,
                updatedAt
        );
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

    public boolean likedByMe(UUID userId) {
        return likes.stream().anyMatch(like -> like.userId().equals(userId));
    }

    public record Snapshot(
            UUID id,
            boolean deleted,
            int rating,
            String content,
            Set<ReviewLikeDomain> likes,
            int commentCount,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
    }
}
