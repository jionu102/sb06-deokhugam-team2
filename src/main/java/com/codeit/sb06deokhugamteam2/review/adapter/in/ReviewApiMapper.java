package com.codeit.sb06deokhugamteam2.review.adapter.in;

import com.codeit.sb06deokhugamteam2.review.adapter.in.dto.CursorPageRequestReviewDto;
import com.codeit.sb06deokhugamteam2.review.adapter.in.dto.CursorPageResponseReviewDto;
import com.codeit.sb06deokhugamteam2.review.adapter.in.dto.ReviewCreateRequest;
import com.codeit.sb06deokhugamteam2.review.adapter.in.dto.ReviewDto;
import com.codeit.sb06deokhugamteam2.review.application.dto.ReviewDetail;
import com.codeit.sb06deokhugamteam2.review.application.dto.ReviewSummary;
import com.codeit.sb06deokhugamteam2.review.application.port.in.command.CreateReviewCommand;
import com.codeit.sb06deokhugamteam2.review.application.port.in.query.ReviewPaginationQuery;
import com.codeit.sb06deokhugamteam2.review.application.port.in.query.ReviewQuery;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class ReviewApiMapper {

    public CreateReviewCommand toCreateReviewCommand(ReviewCreateRequest request) {
        UUID bookId = UUID.fromString(request.bookId());
        UUID userId = UUID.fromString(request.userId());
        Integer rating = request.rating();
        String content = request.content();
        
        return new CreateReviewCommand(bookId, userId, rating, content);
    }

    public ReviewDto toReviewDetailResponse(ReviewDetail detail) {
        UUID id = detail.id();
        UUID bookId = detail.bookId();
        String bookTitle = detail.bookTitle();
        String bookThumbnailUrl = detail.bookThumbnailUrl();
        UUID userId = detail.userId();
        String userNickname = detail.userNickname();
        String content = detail.content();
        Integer rating = detail.rating();
        Integer likeCount = detail.likeCount();
        Integer commentCount = detail.commentCount();
        Boolean likedByMe = detail.likedByMe();
        Instant createdAt = detail.createdAt();
        Instant updatedAt = detail.updatedAt();

        return new ReviewDto(
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

    public ReviewPaginationQuery toReviewPaginationQuery(CursorPageRequestReviewDto request, String header) {
        UUID userId = UUID.fromString(request.userId());
        UUID bookId = UUID.fromString(request.bookId());
        String keyword = request.keyword();
        String orderBy = request.orderBy();
        String direction = request.direction();
        String cursor = request.cursor();
        Instant after = request.after();
        Integer limit = request.limit();
        UUID requestUserId = UUID.fromString(header);

        return new ReviewPaginationQuery(
                userId,
                bookId,
                keyword,
                orderBy,
                direction,
                cursor,
                after,
                limit,
                requestUserId
        );
    }

    public CursorPageResponseReviewDto toReviewSummaryResponse(ReviewSummary summary) {
        List<ReviewDto> content = summary.content()
                .stream()
                .map(this::toReviewDetailResponse)
                .toList();
        String nextCursor = summary.nextCursor();
        String nextAfter = summary.nextAfter();
        Integer size = summary.size();
        Long totalElements = summary.totalElements();
        Boolean hasNext = summary.hasNext();

        return new CursorPageResponseReviewDto(
                content,
                nextCursor,
                nextAfter,
                size,
                totalElements,
                hasNext
        );
    }

    public ReviewQuery toReviewQuery(String request, String header) {
        UUID reviewId = UUID.fromString(request);
        UUID requestUserId = UUID.fromString(header);

        return new ReviewQuery(reviewId, requestUserId);
    }
}
