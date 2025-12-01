package com.codeit.sb06deokhugamteam2.review.adapter.out;

import com.codeit.sb06deokhugamteam2.book.entity.Book;
import com.codeit.sb06deokhugamteam2.review.adapter.out.entity.Review;
import com.codeit.sb06deokhugamteam2.review.application.dto.ReviewDetail;
import com.codeit.sb06deokhugamteam2.review.application.dto.ReviewSummary;
import com.codeit.sb06deokhugamteam2.review.application.port.in.query.ReviewPaginationQuery;
import com.codeit.sb06deokhugamteam2.review.domain.ReviewDomain;
import com.codeit.sb06deokhugamteam2.user.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReviewJpaMapper {

    public Review toReview(ReviewDomain review, Book book, User user) {
        ReviewDomain.Snapshot snapshot = review.createSnapshot();

        return new Review().id(snapshot.id())
                .book(book)
                .user(user)
                .rating(snapshot.rating())
                .content(snapshot.content())
                .likeCount(0)
                .commentCount(0)
                .createdAt(snapshot.createdAt())
                .updatedAt(snapshot.updatedAt());
    }

    public ReviewSummary toReviewSummary(
            List<ReviewDetail> reviewDetails,
            Long totalElements,
            ReviewPaginationQuery query
    ) {
        int size = Math.min(reviewDetails.size(), query.limit());
        List<ReviewDetail> content = reviewDetails.subList(0, size);
        ReviewDetail nextReviewDetail = reviewDetails.get(size - 1);
        String nextCursor = nextCursor(nextReviewDetail, query.orderBy());
        String nextAfter = nextReviewDetail.createdAt().toString();
        Boolean hasNext = reviewDetails.size() > query.limit();

        return new ReviewSummary(
                content,
                nextCursor,
                nextAfter,
                size,
                totalElements,
                hasNext
        );
    }

    private String nextCursor(ReviewDetail nextReviewDetail, String orderBy) {
        if ("rating".equals(orderBy)) {
            return nextReviewDetail.rating().toString();
        }
        return nextReviewDetail.createdAt().toString();
    }
}
