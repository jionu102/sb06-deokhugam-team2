package com.codeit.sb06deokhugamteam2.review.adapter.out;

import com.codeit.sb06deokhugamteam2.review.adapter.out.entity.Review;
import com.codeit.sb06deokhugamteam2.review.domain.ReviewContent;
import com.codeit.sb06deokhugamteam2.review.domain.ReviewDomain;
import com.codeit.sb06deokhugamteam2.review.domain.ReviewRating;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class ReviewMapper {

    public Review toReview(ReviewDomain review) {
        ReviewDomain.Snapshot snapshot = review.createSnapshot();

        return new Review().id(snapshot.id())
                .rating(snapshot.rating().value())
                .content(snapshot.content().value())
                .likeCount(snapshot.likeCount())
                .commentCount(snapshot.commentCount())
                .createdAt(snapshot.createdAt())
                .updatedAt(snapshot.updatedAt());
    }

    public ReviewDomain toReviewDomain(Review review) {
        UUID id = review.id();
        UUID bookId = review.book().getId();
        UUID userId = review.user().getId();
        var rating = new ReviewRating(review.rating());
        var content = new ReviewContent(review.content());
        int likeCount = review.likeCount();
        int commentCount = review.commentCount();
        Instant createdAt = review.createdAt();
        Instant updatedAt = review.updatedAt();

        var snapshot = new ReviewDomain.Snapshot(
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

        return ReviewDomain.loadSnapshot(snapshot);
    }
}
