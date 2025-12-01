package com.codeit.sb06deokhugamteam2.review.adapter.out;

import com.codeit.sb06deokhugamteam2.book.entity.Book;
import com.codeit.sb06deokhugamteam2.review.adapter.out.entity.Review;
import com.codeit.sb06deokhugamteam2.review.application.dto.ReviewDetail;
import com.codeit.sb06deokhugamteam2.review.application.dto.ReviewSummary;
import com.codeit.sb06deokhugamteam2.review.application.port.in.query.ReviewPaginationQuery;
import com.codeit.sb06deokhugamteam2.review.application.port.in.query.ReviewQuery;
import com.codeit.sb06deokhugamteam2.review.application.port.out.ReviewRepository;
import com.codeit.sb06deokhugamteam2.review.domain.ReviewDomain;
import com.codeit.sb06deokhugamteam2.user.entity.User;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.codeit.sb06deokhugamteam2.book.entity.QBook.book;
import static com.codeit.sb06deokhugamteam2.review.adapter.out.entity.QReview.review;
import static com.codeit.sb06deokhugamteam2.review.adapter.out.entity.QReviewLike.reviewLike;
import static com.codeit.sb06deokhugamteam2.user.entity.QUser.user;

@Repository
@Transactional(readOnly = true)
public class ReviewJpaRepository implements ReviewRepository {

    @PersistenceContext
    private EntityManager em;

    private final ReviewJpaMapper reviewMapper;

    public ReviewJpaRepository(ReviewJpaMapper reviewMapper) {
        this.reviewMapper = reviewMapper;
    }

    private <T> JPAQuery<T> select(Expression<T> expr) {
        return new JPAQuery<>(em).select(expr);
    }

    @Override
    public boolean existsByBookIdAndUserId(UUID bookId, UUID userId) {
        Review found = select(review)
                .from(review)
                .innerJoin(review.book, book)
                .innerJoin(review.user, user)
                .where(review.book.id.eq(bookId), review.user.id.eq(userId))
                .fetchFirst();

        return found != null;
    }

    @Override
    @Transactional
    public void addReview(ReviewDomain review) {
        Book book = em.getReference(Book.class, review.bookId());
        User user = em.getReference(User.class, review.userId());
        Review newReview = reviewMapper.toReview(review, book, user);
        em.persist(newReview);
    }

    @Override
    public ReviewDetail findReviewDetailById(UUID reviewId) {
        Review found = select(review)
                .from(review)
                .innerJoin(review.book, book).fetchJoin()
                .innerJoin(review.user, user).fetchJoin()
                .innerJoin(review.likes, reviewLike).fetchJoin()
                .where(review.id.eq(reviewId))
                .fetchOne();

        return reviewMapper.toReviewDetail(found);
    }

    @Override
    public ReviewSummary findReviewSummary(ReviewPaginationQuery query) {
        List<ReviewDetail> reviewDetails = findReviewDetails(query);
        Long totalElements = findTotalElements(query);
        return reviewMapper.toReviewSummary(reviewDetails, totalElements, query);
    }

    private List<ReviewDetail> findReviewDetails(ReviewPaginationQuery query) {
        return select(Projections.constructor(
                ReviewDetail.class,
                review.id,
                review.book.id,
                book.title,
                book.thumbnailUrl,
                review.user.id,
                user.nickname,
                review.content,
                review.rating,
                review.likeCount,
                review.commentCount,
                likedByMe(query.requestUserId()),
                review.createdAt,
                review.updatedAt
        ))
                .from(review)
                .innerJoin(review.book, book)
                .innerJoin(review.user, user)
                .where(
                        bookIdEq(query.bookId()),
                        userIdEq(query.userId()),
                        keywordContains(query.keyword()),
                        cursorExpressions(query),
                        review.deleted.eq(false)
                )
                .orderBy(orderByExpressions(query))
                .limit(query.limit() + 1)
                .fetch();
    }

    private Long findTotalElements(ReviewPaginationQuery query) {
        Long totalElements = select(review.count())
                .from(review)
                .where(
                        userIdEq(query.userId()),
                        bookIdEq(query.bookId()),
                        keywordContains(query.keyword()),
                        review.deleted.eq(false)
                )
                .fetchOne();

        return totalElements == null ? 0L : totalElements;
    }

    private static BooleanExpression likedByMe(UUID requestUserId) {
        return JPAExpressions.selectOne()
                .from(reviewLike)
                .where(
                        reviewLike.review.id.eq(review.id),
                        reviewLike.user.id.eq(requestUserId)
                )
                .exists();
    }

    private static BooleanExpression bookIdEq(UUID bookId) {
        return bookId == null ? null : review.book.id.eq(bookId);
    }

    private static BooleanExpression userIdEq(UUID userId) {
        return userId == null ? null : review.user.id.eq(userId);
    }

    private static BooleanExpression keywordContains(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return review.user.nickname.containsIgnoreCase(keyword)
                .or(review.content.containsIgnoreCase(keyword))
                .or(review.book.title.containsIgnoreCase(keyword));
    }

    private static BooleanExpression cursorExpressions(ReviewPaginationQuery query) {
        if (query.cursor() == null || query.after() == null) {
            return null;
        }

        Instant afterCursor = query.after();
        if ("rating".equals(query.orderBy())) {
            int ratingCursor = Integer.parseInt(query.cursor());
            if ("ASC".equals(query.direction())) {
                return review.rating.gt(ratingCursor)
                        .or(review.rating.eq(ratingCursor).and(review.createdAt.gt(afterCursor)));
            }
            return review.rating.lt(ratingCursor)
                    .or(review.rating.eq(ratingCursor).and(review.createdAt.lt(afterCursor)));
        }
        if ("ASC".equals(query.direction())) {
            return review.createdAt.gt(afterCursor);
        }
        return review.createdAt.lt(afterCursor);
    }

    private static OrderSpecifier<?>[] orderByExpressions(ReviewPaginationQuery query) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        if ("rating".equals(query.orderBy())) {
            var orderByExpression = new OrderSpecifier<>(Order.valueOf(query.direction()), review.rating);
            orderSpecifiers.add(orderByExpression);
        }
        var orderByExpression = new OrderSpecifier<>(Order.valueOf(query.direction()), review.createdAt);
        orderSpecifiers.add(orderByExpression);

        return orderSpecifiers.toArray(OrderSpecifier[]::new);
    }

    @Override
    public ReviewDetail findReviewDetail(ReviewQuery query) {
        return select(Projections.constructor(
                ReviewDetail.class,
                review.id,
                review.book.id,
                book.title,
                book.thumbnailUrl,
                review.user.id,
                user.nickname,
                review.content,
                review.rating,
                review.likeCount,
                review.commentCount,
                likedByMe(query.requestUserId()),
                review.createdAt,
                review.updatedAt
        ))
                .from(review)
                .innerJoin(review.book, book)
                .innerJoin(review.user, user)
                .where(
                        review.id.eq(query.reviewId()),
                        review.deleted.eq(false)
                )
                .fetchOne();
    }
}
