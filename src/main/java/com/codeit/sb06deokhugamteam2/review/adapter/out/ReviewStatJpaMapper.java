package com.codeit.sb06deokhugamteam2.review.adapter.out;

import com.codeit.sb06deokhugamteam2.review.adapter.out.entity.ReviewStat;
import com.codeit.sb06deokhugamteam2.review.domain.ReviewCount;
import com.codeit.sb06deokhugamteam2.review.domain.ReviewStatDomain;
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
        ReviewCount likeCount = new ReviewCount(reviewStat.likeCount());
        ReviewCount commentCount = new ReviewCount(reviewStat.commentCount());
        return new ReviewStatDomain.Snapshot(reviewId, likeCount, commentCount);
    }
}
