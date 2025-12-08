package com.codeit.sb06deokhugamteam2.review.application.service;

import com.codeit.sb06deokhugamteam2.review.application.dto.request.CursorPageRequestReviewDto;
import com.codeit.sb06deokhugamteam2.review.application.dto.response.CursorPageResponseReviewDto;
import com.codeit.sb06deokhugamteam2.review.application.dto.response.ReviewDto;
import com.codeit.sb06deokhugamteam2.review.application.port.in.GetReviewUseCase;
import com.codeit.sb06deokhugamteam2.review.application.port.out.LoadReviewPort;
import com.codeit.sb06deokhugamteam2.review.domain.exception.ReviewNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ReviewQueryService implements GetReviewUseCase {

    private final LoadReviewPort loadReviewPort;

    public ReviewQueryService(LoadReviewPort loadReviewPort) {
        this.loadReviewPort = loadReviewPort;
    }

    @Override
    public CursorPageResponseReviewDto readReviews(CursorPageRequestReviewDto query, String header) {
        UUID requestUserId = UUID.fromString(header);
        String userId = query.userId();
        String bookId = query.bookId();
        String keyword = query.keyword();
        Integer limit = query.limit();
        String orderBy = query.orderBy();

        List<ReviewDto> reviews = loadReviewPort.findAll(query, requestUserId);
        List<ReviewDto> content = extractContent(reviews, limit);
        String nextCursor = extractNextCursor(reviews, limit, orderBy);
        String nextAfter = extractNextAfter(reviews, limit);
        Integer size = content.size();
        Long totalElements = loadReviewPort.count(userId, bookId, keyword);
        Boolean hasNext = calculateHasNext(reviews, limit);

        return new CursorPageResponseReviewDto(
                content,
                nextCursor,
                nextAfter,
                size,
                totalElements,
                hasNext
        );
    }

    private static List<ReviewDto> extractContent(List<ReviewDto> reviews, Integer limit) {
        int size = calculateSize(reviews, limit);
        return reviews.subList(0, size);
    }

    private static int calculateSize(List<ReviewDto> reviews, Integer limit) {
        if (reviews.isEmpty()) {
            return 0;
        }
        if (reviews.size() <= limit) {
            return reviews.size();
        }
        return limit;
    }

    private static String extractNextCursor(List<ReviewDto> reviews, Integer limit, String orderBy) {
        if (reviews.size() <= limit) {
            return null;
        }
        if ("rating".equals(orderBy)) {
            return reviews.get(limit).rating().toString();
        }
        return reviews.get(limit).createdAt().toString();
    }

    private static String extractNextAfter(List<ReviewDto> reviews, Integer limit) {
        if (reviews.size() <= limit) {
            return null;
        }
        return reviews.get(limit).createdAt().toString();
    }

    private static Boolean calculateHasNext(List<ReviewDto> reviews, Integer limit) {
        if (reviews.size() > limit) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    public ReviewDto readReview(String path, String header) {
        UUID reviewId = UUID.fromString(path);
        UUID requestUserId = UUID.fromString(header);

        return loadReviewPort.findById(reviewId, requestUserId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));
    }
}
