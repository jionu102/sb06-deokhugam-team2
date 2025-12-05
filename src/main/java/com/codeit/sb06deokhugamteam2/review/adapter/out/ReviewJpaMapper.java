package com.codeit.sb06deokhugamteam2.review.adapter.out;

import com.codeit.sb06deokhugamteam2.review.adapter.out.entity.Review;
import com.codeit.sb06deokhugamteam2.review.adapter.out.entity.ReviewStat;
import com.codeit.sb06deokhugamteam2.review.domain.ReviewContent;
import com.codeit.sb06deokhugamteam2.review.domain.ReviewDomain;
import com.codeit.sb06deokhugamteam2.review.domain.ReviewRating;
import com.codeit.sb06deokhugamteam2.review.domain.ReviewStatDomain;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class ReviewJpaMapper {

    private final ReviewStatJpaMapper reviewStatMapper;

    public ReviewJpaMapper(ReviewStatJpaMapper reviewStatMapper) {
        this.reviewStatMapper = reviewStatMapper;
    }

    public Review toEntity(ReviewDomain.Snapshot reviewSnapshot) {
        ReviewStat reviewStat = reviewStatMapper.toEntity(reviewSnapshot.reviewStatSnapshot());

        return new Review().id(reviewSnapshot.id())
                .reviewStat(reviewStat)
                .rating(reviewSnapshot.rating().value())
                .content(reviewSnapshot.content().value())
                .createdAt(reviewSnapshot.createdAt())
                .updatedAt(reviewSnapshot.updatedAt());
    }

    public ReviewDomain.Snapshot toDomainSnapshot(Review review) {
        UUID id = review.id();
        UUID bookId = review.book().getId();
        UUID userId = review.user().getId();
        ReviewStatDomain.Snapshot reviewStatSnapshot = reviewStatMapper.toDomainSnapshot(review.reviewStat());
        var rating = new ReviewRating(review.rating());
        var content = new ReviewContent(review.content());
        Instant createdAt = review.createdAt();
        Instant updatedAt = review.updatedAt();

        return new ReviewDomain.Snapshot(
                id,
                bookId,
                userId,
                reviewStatSnapshot,
                rating,
                content,
                createdAt,
                updatedAt
        );
    }
}
