package com.codeit.sb06deokhugamteam2.review.adapter.out.mapper;

import com.codeit.sb06deokhugamteam2.review.adapter.out.entity.ReviewStat;
import com.codeit.sb06deokhugamteam2.review.domain.model.ReviewCountDomain;
import com.codeit.sb06deokhugamteam2.review.domain.model.ReviewStatDomain;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ReviewStatJpaMapper {

    public ReviewStat toEntity(ReviewStatDomain.Snapshot reviewStatSnapshot) {
        return new ReviewStat()
                .id(reviewStatSnapshot.reviewId())
                .likeCount(reviewStatSnapshot.likeCount().value())
                .commentCount(reviewStatSnapshot.commentCount().value());
    }

    public ReviewStatDomain.Snapshot toDomainSnapshot(ReviewStat reviewStat) {
        UUID reviewId = reviewStat.id();
        ReviewCountDomain likeCount = new ReviewCountDomain(reviewStat.likeCount());
        ReviewCountDomain commentCount = new ReviewCountDomain(reviewStat.commentCount());
        return new ReviewStatDomain.Snapshot(reviewId, likeCount, commentCount);
    }
}
