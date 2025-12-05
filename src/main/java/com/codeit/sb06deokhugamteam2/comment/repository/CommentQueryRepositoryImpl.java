package com.codeit.sb06deokhugamteam2.comment.repository;

import com.codeit.sb06deokhugamteam2.comment.entity.Comment;
import com.codeit.sb06deokhugamteam2.comment.entity.QComment;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CommentQueryRepositoryImpl implements CommentQueryRepository {

    private final JPAQueryFactory query;

    @Override
    public List<Comment> findCommentsByCursor(
            UUID reviewId,
            Instant after,
            UUID cursor,
            String direction,
            int size
    ) {

        QComment comment = QComment.comment;

        BooleanBuilder condition = new BooleanBuilder();
        condition.and(comment.review.id.eq(reviewId));


        boolean isAsc = direction.equalsIgnoreCase("ASC");

        if (after != null) {
            if (!isAsc) {
                condition.and(
                        comment.createdAt.lt(after)
                                .or(comment.createdAt.eq(after)
                                        .and(cursor != null ? comment.id.lt(cursor) : Expressions.TRUE)
                                )
                );
            } else {
                condition.and(
                        comment.createdAt.gt(after)
                                .or(comment.createdAt.eq(after)
                                        .and(cursor != null ? comment.id.gt(cursor) : Expressions.TRUE)
                                )
                );
            }
        }

        OrderSpecifier<?> order1 = !isAsc ? comment.createdAt.desc() : comment.createdAt.asc();
        OrderSpecifier<?> order2 = !isAsc ? comment.id.desc() : comment.id.asc();

        return query
                .selectFrom(comment)
                .where(condition)
                .orderBy(order1, order2)
                .limit(size)
                .fetch();
    }

    @Override
    public long countByReviewId(UUID reviewId) {
        QComment comment = QComment.comment;

        return query
                .select(comment.count())
                .from(comment)
                .where(
                        comment.review.id.eq(reviewId)
                )
                .fetchOne();
    }

    private BooleanExpression cursorCondition(QComment comment, Instant after, UUID cursor) {

        if (after == null || cursor == null) {
            return null;
        }

        return comment.createdAt.eq(after)
                .and(comment.id.lt(cursor));
    }
}
