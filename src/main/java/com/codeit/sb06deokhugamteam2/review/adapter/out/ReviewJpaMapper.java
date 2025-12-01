package com.codeit.sb06deokhugamteam2.review.adapter.out;

import com.codeit.sb06deokhugamteam2.book.entity.Book;
import com.codeit.sb06deokhugamteam2.review.adapter.out.entity.Review;
import com.codeit.sb06deokhugamteam2.review.application.dto.ReviewDetail;
import com.codeit.sb06deokhugamteam2.review.application.dto.ReviewSummary;
import com.codeit.sb06deokhugamteam2.review.application.port.in.query.ReviewPaginationQuery;
import com.codeit.sb06deokhugamteam2.review.domain.ReviewDomain;
import com.codeit.sb06deokhugamteam2.user.entity.User;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class ReviewJpaMapper {

    public Review toReview(ReviewDomain review, Book book, User user) {
        ReviewDomain.Snapshot snapshot = review.createSnapshot();

        return new Review().id(snapshot.id())
                .book(book)
                .user(user)
                .rating(snapshot.rating())
                .content(snapshot.content())
                .likeCount(snapshot.likeCount())
                .commentCount(snapshot.commentCount())
                .createdAt(snapshot.createdAt())
                .updatedAt(snapshot.updatedAt())
                .deleted(snapshot.deleted());
    }

    public ReviewDetail toReviewDetail(Review review) {
        UUID id = review.id();
        UUID bookId = review.book().getId();
        String bookTitle = review.book().getTitle();
        String bookThumbnailUrl = review.book().getThumbnailUrl();
        UUID userId = review.user().getId();
        String userNickname = review.user().getNickname();
        String content = review.content();
        Integer rating = review.rating();
        Integer likeCount = review.likeCount();
        Integer commentCount = review.commentCount();
        Boolean likedByMe = review.likes().contains(userId);
        Instant createdAt = review.createdAt();
        Instant updatedAt = review.updatedAt();

        return new ReviewDetail(
                id,
                bookId,
                bookTitle,
                bookThumbnailUrl,
                userId,
                userNickname,
                content,
                rating,
                likeCount,
                commentCount,
                likedByMe,
                createdAt,
                updatedAt
        );
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
