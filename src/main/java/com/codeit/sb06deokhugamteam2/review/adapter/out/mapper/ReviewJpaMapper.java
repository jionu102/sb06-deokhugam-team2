package com.codeit.sb06deokhugamteam2.review.adapter.out.mapper;

import com.codeit.sb06deokhugamteam2.review.adapter.out.entity.Review;
import com.codeit.sb06deokhugamteam2.review.adapter.out.entity.ReviewStat;
import com.codeit.sb06deokhugamteam2.review.domain.model.ReviewContentDomain;
import com.codeit.sb06deokhugamteam2.review.domain.model.ReviewDomain;
import com.codeit.sb06deokhugamteam2.review.domain.model.ReviewRatingDomain;
import com.codeit.sb06deokhugamteam2.review.domain.model.ReviewStatDomain;
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
                .updatedAt(reviewSnapshot.updatedAt())
                .deleted(reviewSnapshot.isHidden());
    }

    public ReviewDomain.Snapshot toDomainSnapshot(Review review) {
        UUID id = review.id();
        UUID bookId = review.book().getId();
        UUID userId = review.user().getId();
        ReviewStatDomain.Snapshot reviewStatSnapshot = reviewStatMapper.toDomainSnapshot(review.reviewStat());
        var rating = new ReviewRatingDomain(review.rating());
        var content = new ReviewContentDomain(review.content());
        Instant createdAt = review.createdAt();
        Instant updatedAt = review.updatedAt();
        boolean isHidden = review.deleted();

        return new ReviewDomain.Snapshot(
                id,
                bookId,
                userId,
                reviewStatSnapshot,
                rating,
                content,
                createdAt,
                updatedAt,
                isHidden
        );
    }
}
