package com.codeit.sb06deokhugamteam2.review.domain.model;

import com.codeit.sb06deokhugamteam2.review.domain.event.ReviewDeletedEvent;
import com.codeit.sb06deokhugamteam2.review.domain.event.ReviewLikeCanceledEvent;
import com.codeit.sb06deokhugamteam2.review.domain.event.ReviewLikedEvent;
import com.codeit.sb06deokhugamteam2.review.domain.exception.ReviewPermissionDeniedException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReviewDomain {

    private final UUID id;
    private final UUID bookId;
    private final UUID userId;
    private final ReviewStatDomain reviewStat;
    private ReviewRatingDomain rating;
    private ReviewContentDomain content;
    private final Instant createdAt;
    private Instant updatedAt;
    private boolean hidden;
    private final List<Object> events = new ArrayList<>();

    public ReviewDomain(
            UUID id,
            UUID bookId,
            UUID userId,
            ReviewStatDomain reviewStat,
            ReviewRatingDomain rating,
            ReviewContentDomain content,
            Instant createdAt,
            Instant updatedAt,
            boolean hidden
    ) {
        this.id = id;
        this.bookId = bookId;
        this.userId = userId;
        this.reviewStat = reviewStat;
        this.rating = rating;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.hidden = hidden;
    }

    public static ReviewDomain create(UUID bookId, UUID userId, ReviewRatingDomain rating, ReviewContentDomain content) {
        UUID id = UUID.randomUUID();
        ReviewStatDomain reviewStat = ReviewStatDomain.create(id);
        Instant createdAt = Instant.now();
        Instant updatedAt = createdAt;
        boolean isHidden = false;

        return new ReviewDomain(
                id,
                bookId,
                userId,
                reviewStat,
                rating,
                content,
                createdAt,
                updatedAt,
                isHidden
        );
    }

    public static ReviewDomain from(Snapshot reviewSnapshot) {
        UUID id = reviewSnapshot.id();
        UUID bookId = reviewSnapshot.bookId();
        UUID userId = reviewSnapshot.userId();
        ReviewStatDomain reviewStat = ReviewStatDomain.from(reviewSnapshot.reviewStatSnapshot());
        ReviewRatingDomain rating = reviewSnapshot.rating();
        ReviewContentDomain content = reviewSnapshot.content();
        Instant createdAt = reviewSnapshot.createdAt();
        Instant updatedAt = reviewSnapshot.updatedAt();
        boolean isHidden = reviewSnapshot.isHidden();

        return new ReviewDomain(
                id,
                bookId,
                userId,
                reviewStat,
                rating,
                content,
                createdAt,
                updatedAt,
                isHidden
        );
    }

    public Snapshot toSnapshot() {
        ReviewStatDomain.Snapshot reviewStatSnapshot = this.reviewStat.toSnapshot();

        return new Snapshot(
                id,
                bookId,
                userId,
                reviewStatSnapshot,
                rating,
                content,
                createdAt,
                updatedAt,
                hidden
        );
    }

    public ReviewDomain verifyOwner(UUID requestUserId) {
        if (!userId.equals(requestUserId)) {
            throw new ReviewPermissionDeniedException(requestUserId);
        }
        return this;
    }

    public ReviewDomain edit(ReviewRatingDomain rating, ReviewContentDomain content) {
        this.rating = rating;
        this.content = content;
        this.updatedAt = Instant.now();
        return this;
    }

    public ReviewDomain hide() {
        hidden = true;
        return this;
    }

    public ReviewDomain delete() {
        events.add(new ReviewDeletedEvent(id));
        return this;
    }

    public ReviewDomain increaseReviewLike() {
        reviewStat.increaseLikeCount();
        Instant likedAt = Instant.now();
        events.add(new ReviewLikedEvent(id, userId, likedAt));
        return this;
    }

    public ReviewDomain decreaseReviewLike() {
        reviewStat.decreaseLikeCount();
        events.add(new ReviewLikeCanceledEvent(id, userId));
        return this;
    }

    public List<Object> events() {
        return events;
    }

    public void clearEvents() {
        events.clear();
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

    public ReviewRatingDomain rating() {
        return rating;
    }

    public ReviewContentDomain content() {
        return content;
    }

    public boolean isHidden() {
        return hidden;
    }

    public record Snapshot(
            UUID id,
            UUID bookId,
            UUID userId,
            ReviewStatDomain.Snapshot reviewStatSnapshot,
            ReviewRatingDomain rating,
            ReviewContentDomain content,
            Instant createdAt,
            Instant updatedAt,
            boolean isHidden
    ) {
    }
}
