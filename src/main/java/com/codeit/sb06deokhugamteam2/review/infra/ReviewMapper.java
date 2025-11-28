package com.codeit.sb06deokhugamteam2.review.infra;

import com.codeit.sb06deokhugamteam2.book.entity.Book;
import com.codeit.sb06deokhugamteam2.review.domain.ReviewCreationCommand;
import com.codeit.sb06deokhugamteam2.review.domain.ReviewDetail;
import com.codeit.sb06deokhugamteam2.review.domain.ReviewDomain;
import com.codeit.sb06deokhugamteam2.review.infra.persistence.entity.Review;
import com.codeit.sb06deokhugamteam2.review.infra.web.dto.ReviewCreateRequest;
import com.codeit.sb06deokhugamteam2.review.infra.web.dto.ReviewDto;
import com.codeit.sb06deokhugamteam2.user.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Component
public class ReviewMapper {

    public ReviewCreationCommand toCreationCommand(ReviewCreateRequest request) {
        UUID bookId = request.bookId();
        UUID userId = request.userId();
        final Integer rating = request.rating();
        final String content = request.content();
        return new ReviewCreationCommand(bookId, userId, rating, content);
    }

    public Review toEntity(ReviewDomain.Snapshot snapshot, Book book, User user) {
        return new Review().id(snapshot.id())
                .rating(snapshot.rating())
                .content(snapshot.content())
                .likeCount(snapshot.likes().size())
                .commentCount(snapshot.commentCount())
                .createdAt(snapshot.createdAt().atZone(ZoneId.systemDefault()).toInstant())
                .updatedAt(snapshot.updatedAt().atZone(ZoneId.systemDefault()).toInstant())
                .deleted(snapshot.deleted())
                .book(book)
                .user(user);
    }

    public ReviewDetail toDomain(Review review, boolean likedByMe) {
        UUID id = review.id();
        UUID bookId = review.book().getId();
        final String bookTitle = review.book().getTitle();
        final String bookThumbnailUrl = review.book().getThumbnailUrl();
        UUID userId = review.user().getId();
        final String userNickname = review.user().getNickname();
        final String content = review.content();
        final int rating = review.rating();
        final int likeCount = review.likeCount();
        final int commentCount = review.commentCount();
        LocalDateTime createdAt = review.createdAt().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime updatedAt = review.updatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime();
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

    public ReviewDto toDto(ReviewDetail reviewDetail) {
        UUID id = reviewDetail.id();
        UUID bookId = reviewDetail.bookId();
        final String bookTitle = reviewDetail.bookTitle();
        final String bookThumbnailUrl = reviewDetail.bookThumbnailUrl();
        UUID userId = reviewDetail.userId();
        final String userNickname = reviewDetail.userNickname();
        final String content = reviewDetail.content();
        final int rating = reviewDetail.rating();
        final int likeCount = reviewDetail.likeCount();
        final int commentCount = reviewDetail.commentCount();
        final boolean likedByMe = reviewDetail.likedByMe();
        LocalDateTime createdAt = reviewDetail.createdAt();
        LocalDateTime updatedAt = reviewDetail.updatedAt();
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
}
