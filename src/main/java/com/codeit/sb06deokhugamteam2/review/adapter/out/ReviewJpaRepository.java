package com.codeit.sb06deokhugamteam2.review.adapter.out;

import com.codeit.sb06deokhugamteam2.book.entity.Book;
import com.codeit.sb06deokhugamteam2.review.adapter.out.entity.QReview;
import com.codeit.sb06deokhugamteam2.review.adapter.out.entity.Review;
import com.codeit.sb06deokhugamteam2.review.application.dto.CursorPageRequestReviewDto;
import com.codeit.sb06deokhugamteam2.review.application.dto.CursorPageResponseReviewDto;
import com.codeit.sb06deokhugamteam2.review.application.dto.ReviewDto;
import com.codeit.sb06deokhugamteam2.review.application.port.out.QueryReviewPort;
import com.codeit.sb06deokhugamteam2.review.domain.ReviewDomain;
import com.codeit.sb06deokhugamteam2.review.domain.port.ReviewRepository;
import com.codeit.sb06deokhugamteam2.user.entity.User;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.codeit.sb06deokhugamteam2.book.entity.QBook.book;
import static com.codeit.sb06deokhugamteam2.review.adapter.out.entity.QReview.review;
import static com.codeit.sb06deokhugamteam2.review.adapter.out.entity.QReviewLike.reviewLike;
import static com.codeit.sb06deokhugamteam2.user.entity.QUser.user;

@Repository
@Transactional(readOnly = true)
public class ReviewJpaRepository implements ReviewRepository, QueryReviewPort {

    @PersistenceContext
    private EntityManager em;

    private final ReviewMapper reviewMapper;

    public ReviewJpaRepository(ReviewMapper reviewMapper) {
        this.reviewMapper = reviewMapper;
    }

    private <T> JPAQuery<T> select(Expression<T> expr) {
        return new JPAQuery<>(em).select(expr);
    }

    @Override
    public boolean existsByBookIdAndUserId(UUID bookId, UUID userId) {
        Review reviewEntity = select(review)
                .from(review)
                .innerJoin(review.book, book)
                .innerJoin(review.user, user)
                .where(review.book.id.eq(bookId), review.user.id.eq(userId))
                .fetchFirst();

        return reviewEntity != null;
    }

    @Override
    @Transactional
    public void save(ReviewDomain review) {
        Review reviewEntity = reviewMapper.toReview(review);
        Book book = em.getReference(Book.class, review.bookId());
        User user = em.getReference(User.class, review.userId());
        reviewEntity = reviewEntity.book(book).user(user);
        em.persist(reviewEntity);
    }

    @Override
    public Optional<ReviewDto> findById(UUID reviewId, UUID requestUserId) {
        ReviewDto reviewDto = findById(requestUserId, QReview.review.id.eq(reviewId));
        return Optional.ofNullable(reviewDto);
    }

    private ReviewDto findById(UUID requestUserId, Predicate... predicates) {
        return select(reviewProjection(requestUserId))
                .from(review)
                .innerJoin(review.book, book)
                .innerJoin(review.user, user)
                .where(predicates)
                .fetchOne();
    }

    private static Expression<ReviewDto> reviewProjection(UUID requestUserId) {
        return Projections.constructor(
                ReviewDto.class,
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
    public CursorPageResponseReviewDto findAll(CursorPageRequestReviewDto request, UUID requestUserId) {
        String bookId = request.bookId();
        String userId = request.userId();
        String keyword = request.keyword();
        String orderBy = request.orderBy();
        String cursor = request.cursor();
        Instant after = request.after();
        String direction = request.direction();
        Integer limit = request.limit();

        List<ReviewDto> reviews = findAll(
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
        Long count = count(userId, bookId, keyword);

        List<ReviewDto> content = extractContent(reviews, limit);
        String nextCursor = extractNextCursor(reviews, limit, orderBy);
        String nextAfter = extractNextAfter(reviews, limit);
        Integer size = calculateSize(reviews, limit);
        Long totalElements = count;
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
        return select(reviewProjection(requestUserId))
                .from(review)
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

    private Long count(String userId, String bookId, String keyword) {
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
    public Optional<ReviewDomain> findById(UUID reviewId) {
        Review reviewEntity = em.find(Review.class, reviewId);
        return Optional.ofNullable(reviewEntity).map(reviewMapper::toReviewDomain);
    }

    @Override
    @Transactional
    public void delete(ReviewDomain review) {
        Review reviewEntity = em.getReference(Review.class, review.id());
        em.remove(reviewEntity);
    }

    @Override
    public Optional<ReviewDomain> findByIdWithoutDeleted(UUID reviewId) {
        String sql = "SELECT * FROM reviews r WHERE r.id = :reviewId";
        Review reviewEntity = em.unwrap(Session.class)
                .createNativeQuery(sql, Review.class)
                .setParameter("reviewId", reviewId)
                .getSingleResult();
        return Optional.ofNullable(reviewEntity).map(reviewMapper::toReviewDomain);
    }

    @Override
    @Transactional
    public void hardDelete(ReviewDomain review) {
        em.createNativeQuery("DELETE FROM reviews r WHERE r.id = :reviewId")
                .setParameter("reviewId", review.id())
                .executeUpdate();
    }

    @Override
    @Transactional
    public void update(ReviewDomain review) {
        ReviewDomain.Snapshot snapshot = review.createSnapshot();
        Review reviewEntity = em.find(Review.class, snapshot.id());
        reviewEntity.rating(snapshot.rating().value())
                .content(snapshot.content().value());
    }
}
