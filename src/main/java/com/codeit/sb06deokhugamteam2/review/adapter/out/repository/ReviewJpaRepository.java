package com.codeit.sb06deokhugamteam2.review.adapter.out.repository;

import com.codeit.sb06deokhugamteam2.book.entity.Book;
import com.codeit.sb06deokhugamteam2.review.adapter.out.entity.Review;
import com.codeit.sb06deokhugamteam2.review.adapter.out.mapper.ReviewJpaMapper;
import com.codeit.sb06deokhugamteam2.review.application.dto.request.CursorPageRequestReviewDto;
import com.codeit.sb06deokhugamteam2.review.application.dto.response.ReviewDto;
import com.codeit.sb06deokhugamteam2.review.application.port.out.LoadReviewPort;
import com.codeit.sb06deokhugamteam2.review.application.port.out.SaveReviewPort;
import com.codeit.sb06deokhugamteam2.review.domain.model.ReviewDomain;
import com.codeit.sb06deokhugamteam2.user.entity.User;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.codeit.sb06deokhugamteam2.book.entity.QBook.book;
import static com.codeit.sb06deokhugamteam2.like.adapter.out.entity.QReviewLike.reviewLike;
import static com.codeit.sb06deokhugamteam2.review.adapter.out.entity.QReview.review;
import static com.codeit.sb06deokhugamteam2.review.adapter.out.entity.QReviewStat.reviewStat;
import static com.codeit.sb06deokhugamteam2.user.entity.QUser.user;

@Repository
public class ReviewJpaRepository implements LoadReviewPort, SaveReviewPort {

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
        Integer reviewExists = select(Expressions.ONE)
                .from(review)
                .where(review.book.id.eq(bookId), review.user.id.eq(userId))
                .fetchOne();

        return reviewExists != null;
    }

    @Override
    public void save(ReviewDomain.Snapshot reviewSnapshot) {
        Review reviewEntity = reviewMapper.toEntity(reviewSnapshot);
        Book bookEntity = em.getReference(Book.class, reviewSnapshot.bookId());
        User userEntity = em.getReference(User.class, reviewSnapshot.userId());
        reviewEntity.book(bookEntity).user(userEntity);
        em.persist(reviewEntity);
    }

    @Override
    public Optional<ReviewDto> findById(UUID reviewId, UUID requestUserId) {
        ReviewDto reviewDto = findById(requestUserId, review.id.eq(reviewId));
        return Optional.ofNullable(reviewDto);
    }

    private ReviewDto findById(UUID requestUserId, Predicate... predicates) {
        return select(reviewDtoProjection(requestUserId))
                .from(review)
                .innerJoin(review.book, book)
                .innerJoin(review.user, user)
                .innerJoin(review.reviewStat, reviewStat)
                .where(predicates)
                .fetchOne();
    }

    private static Expression<ReviewDto> reviewDtoProjection(UUID requestUserId) {
        return Projections.constructor(
                ReviewDto.class,
                review.id,
                review.book.id,
                review.book.title,
                review.book.thumbnailUrl,
                review.user.id,
                review.user.nickname,
                review.content,
                review.rating,
                review.reviewStat.likeCount,
                review.reviewStat.commentCount,
                likedByMe(requestUserId),
                review.createdAt,
                review.updatedAt
        );
    }

    private static BooleanExpression likedByMe(UUID requestUserId) {
        if (requestUserId == null) {
            return Expressions.FALSE;
        }
        return JPAExpressions.selectOne()
                .from(reviewLike)
                .where(
                        reviewLike.review.id.eq(review.id),
                        reviewLike.user.id.eq(requestUserId)
                )
                .exists();
    }

    @Override
    public List<ReviewDto> findAll(CursorPageRequestReviewDto request, UUID requestUserId) {
        String bookId = request.bookId();
        String userId = request.userId();
        String keyword = request.keyword();
        String orderBy = request.orderBy();
        String cursor = request.cursor();
        Instant after = request.after();
        String direction = request.direction();
        Integer limit = request.limit();

        return findAll(
                bookId,
                userId,
                keyword,
                orderBy,
                cursor,
                after,
                direction,
                limit,
                requestUserId
        );
    }

    private List<ReviewDto> findAll(
            String bookId,
            String userId,
            String keyword,
            String orderBy,
            String cursor,
            Instant after,
            String direction,
            Integer limit,
            UUID requestUserId
    ) {
        Predicate[] predicates = {
                bookIdEq(bookId),
                userIdEq(userId),
                keywordContains(keyword),
                cursorExpressions(orderBy, cursor, after, direction)
        };
        return findAll(requestUserId, predicates, orderBy, direction, limit);
    }

    private List<ReviewDto> findAll(
            UUID requestUserId,
            Predicate[] predicates,
            String orderBy,
            String direction,
            Integer limit
    ) {
        return select(reviewDtoProjection(requestUserId))
                .from(review)
                .innerJoin(review.reviewStat, reviewStat)
                .innerJoin(review.book, book)
                .innerJoin(review.user, user)
                .where(predicates)
                .orderBy(orderByExpressions(orderBy, direction))
                .limit(limit + 1)
                .fetch();
    }

    private static BooleanExpression bookIdEq(String bookId) {
        return bookId == null ? null : review.book.id.eq(UUID.fromString(bookId));
    }

    private static BooleanExpression userIdEq(String userId) {
        return userId == null ? null : review.user.id.eq(UUID.fromString(userId));
    }

    private static BooleanExpression keywordContains(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return review.user.nickname.containsIgnoreCase(keyword)
                .or(review.content.containsIgnoreCase(keyword))
                .or(review.book.title.containsIgnoreCase(keyword));
    }

    private static BooleanExpression cursorExpressions(String orderBy, String cursor, Instant after, String direction) {
        if (cursor == null || after == null) {
            return null;
        }

        if ("rating".equals(orderBy)) {
            int ratingCursor = Integer.parseInt(cursor);
            if ("ASC".equals(direction)) {
                return review.rating.gt(ratingCursor)
                        .or(review.rating.eq(ratingCursor).and(review.createdAt.goe(after)));
            }
            return review.rating.lt(ratingCursor)
                    .or(review.rating.eq(ratingCursor).and(review.createdAt.loe(after)));
        }
        if ("ASC".equals(direction)) {
            return review.createdAt.goe(after);
        }
        return review.createdAt.loe(after);
    }

    private static OrderSpecifier<?>[] orderByExpressions(String orderBy, String direction) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        if ("rating".equals(orderBy)) {
            var orderByExpression = new OrderSpecifier<>(Order.valueOf(direction), review.rating);
            orderSpecifiers.add(orderByExpression);
        }
        var orderByExpression = new OrderSpecifier<>(Order.valueOf(direction), review.createdAt);
        orderSpecifiers.add(orderByExpression);

        return orderSpecifiers.toArray(OrderSpecifier[]::new);
    }

    @Override
    public long count(String userId, String bookId, String keyword) {
        Long count = select(review.count())
                .from(review)
                .where(
                        userIdEq(userId),
                        bookIdEq(bookId),
                        keywordContains(keyword)
                )
                .fetchOne();

        return count == null ? 0L : count;
    }

    @Override
    public Optional<ReviewDomain.Snapshot> findById(UUID reviewId) {
        Review reviewEntity = select(review)
                .from(review)
                .innerJoin(review.reviewStat, reviewStat).fetchJoin()
                .where(review.id.eq(reviewId))
                .fetchOne();
        return Optional.ofNullable(reviewEntity)
                .map(reviewMapper::toDomainSnapshot);
    }

    @Override
    public void softDelete(UUID reviewId) {
        Review reviewEntity = em.find(Review.class, reviewId);
        reviewEntity.deleted(Boolean.TRUE);
    }

    @Override
    public Optional<ReviewDomain.Snapshot> findByIdWithoutDeleted(UUID reviewId) {
        Review reviewEntity = em.unwrap(Session.class)
                .createNativeQuery("""
                        SELECT r.*, rs.*
                        FROM reviews r
                        INNER JOIN review_stats rs ON r.id = rs.review_id
                        WHERE r.id = :reviewId
                        """, Review.class)
                .addEntity("r", Review.class)
                .addJoin("rs", "r.reviewStat")
                .setParameter("reviewId", reviewId)
                .getSingleResult();
        return Optional.ofNullable(reviewEntity)
                .map(reviewMapper::toDomainSnapshot);
    }

    @Override
    public void hardDelete(UUID reviewId) {
        Review reviewEntity = em.getReference(Review.class, reviewId);
        em.remove(reviewEntity);
    }

    @Override
    public void update(ReviewDomain.Snapshot reviewSnapshot) {
        Review reviewEntity = reviewMapper.toEntity(reviewSnapshot);
        Book bookEntity = em.getReference(Book.class, reviewSnapshot.bookId());
        User userEntity = em.getReference(User.class, reviewSnapshot.userId());
        reviewEntity.book(bookEntity).user(userEntity);
        em.merge(reviewEntity);
    }

    @Override
    public Optional<ReviewDomain.Snapshot> findByIdForUpdate(UUID reviewId) {
        Review reviewEntity = select(review)
                .from(review)
                .innerJoin(review.reviewStat, reviewStat).fetchJoin()
                .where(review.id.eq(reviewId))
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .fetchOne();
        return Optional.ofNullable(reviewEntity)
                .map(reviewMapper::toDomainSnapshot);
    }
}
