package com.codeit.sb06deokhugamteam2.review.domain.model;

import java.util.UUID;

public record ReviewLikeNotificationDomain(UUID reviewId, UUID userId, String reviewTitle, String content) {

    private static final String REVIEW_LIKE_NOTIFICATION_CONTENT_FORMAT = "[%s]님이 나의 리뷰를 좋아합니다.";

    public static ReviewLikeNotificationDomain create(
            UUID reviewId,
            UUID userId,
            String reviewTitle,
            String nickname
    ) {
        String content = REVIEW_LIKE_NOTIFICATION_CONTENT_FORMAT.formatted(nickname);
        return new ReviewLikeNotificationDomain(reviewId, userId, reviewTitle, content);
    }
}
