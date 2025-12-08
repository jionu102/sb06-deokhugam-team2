package com.codeit.sb06deokhugamteam2.user.repository;


import com.codeit.sb06deokhugamteam2.common.enums.PeriodType;
import com.codeit.sb06deokhugamteam2.user.dto.CursorPageResponse;
import com.codeit.sb06deokhugamteam2.user.dto.PowerUserDto;
import com.codeit.sb06deokhugamteam2.user.entity.User;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.codeit.sb06deokhugamteam2.comment.entity.QComment.comment;
import static com.codeit.sb06deokhugamteam2.review.adapter.out.entity.QReview.review;


import static com.codeit.sb06deokhugamteam2.like.adapter.out.entity.QReviewLike.reviewLike;
import static com.codeit.sb06deokhugamteam2.user.entity.QUser.user;


@Repository
@RequiredArgsConstructor
public class UserQueryRepository {

    private final JPAQueryFactory queryFactory;

    public CursorPageResponse<PowerUserDto> findPowerUsers(
            PeriodType period, String direction, String cursor, String afterString, Pageable pageable) {

        LocalDateTime after = afterString != null ? LocalDateTime.parse(afterString) : LocalDateTime.now();

        // 리뷰 평점 합계 (reviewScoreSum), double타입
        var reviewScoreSum = review.rating.sum().doubleValue().coalesce(0.0);

        // 받은 좋아요 수 (likeCount), 중복좋아요 방지 countDistinct, integer타입
        var likeCount = reviewLike.countDistinct().coalesce(0L);

        //댓글수는 리뷰엔티티에 존재,(카운터캐시)댓글갯수 따로 조회필요없이 리뷰목록조회만으로 댓글수 확인가능
        var commentCount = comment.countDistinct().coalesce(0L); //integer타입

        //최종점수 calculatedScore는 실수형(Double)
        var calculatedScore = reviewScoreSum.doubleValue().multiply(0.5) // * 0.5(리뷰인기점수)
                .add(likeCount.doubleValue().multiply(0.2)) // + 좋아요 수 * 0.2
                .add(commentCount.doubleValue().multiply(0.3)); // + 댓글 수 * 0.3

        // 파워 유저 데이터 조회 및 집계
        List<PowerUserDto> content = queryFactory
                .select(Projections.constructor(PowerUserDto.class,
                        user.id,
                        user.nickname,
                        Expressions.constant(period.toString()),
                        user.createdAt,
                        // Rank는 임시로 1L 고정. 정확한 랭킹 로직 논의 필요
                        Expressions.constant(1L),

                        calculatedScore,
                        reviewScoreSum,
                        likeCount,
                        commentCount
                ))
                .from(user)

                // LEFT JOIN을 사용하여 리뷰, 좋아요, 댓글 정보가 없어도 유저를 포함
                .leftJoin(review).on(review.user.eq(user))
                .leftJoin(reviewLike).on(reviewLike.id.reviewId.eq(review.id))
                .leftJoin(comment).on(comment.review.eq(review))

                // 기간 필터링 조건 추가 (createdAt을 사용하여 커서 구현)
                .where(isAfterCursor(after),filterByPeriod(period))

                // 유저별로 그룹화하여 집계 수행 (Score 계산의 필수 단계)
                .groupBy(user.id, user.nickname, user.createdAt)

                // Score 기준으로 내림차순 정렬 (파워 유저 목록)
                .orderBy(calculatedScore.desc())
                .limit(pageable.getPageSize() + 1) // 다음 페이지 유무 확인을 위해 limit+1
                .fetch();

        // 커서 페이지네이션 로직 (hasNext, nextCursor 계산)
        boolean hasNext = content.size() > pageable.getPageSize();
        String nextAfterString = null;

        if (hasNext) {
            content.remove(pageable.getPageSize()); // 마지막 요소 제거
            // 다음 커서 값 설정 (Score 정렬이 복합적일 경우 커서 값도 복합적으로 설정)
            // 현재는 createdAt 기준으로만 커서를 설정한다고 가정
            nextAfterString = content.get(content.size() - 1).createdAt().toString();
        }

        // CursorPageResponse 객체 반환
        return new CursorPageResponse<>(
                content,
                // Cursor는 Score와 같은 정렬 기준이 되지만, 여기서는 null로
                null,
                nextAfterString,
                content.size(),
                (long) content.size(), // 정확한 총 개수는 별도 Count 쿼리 필요
                hasNext
        );
    }

    //논리 삭제 후 1일이 경과한 사용자 ID 목록을 조회 (물리 삭제 배치 대상)
    public List<UUID> findSoftDeletedUsersForHardDelete(LocalDateTime olderThan) {
        return queryFactory
                .select(user.id)
                .from(user)
                .where(user.deletedAt.isNotNull(),  //deleted_at 값이 있는 경우만
                        user.deletedAt.lt(olderThan)) //1일이상 경과한 사용자
                .fetch();
    }

    //커서페이지네이션 위해 설정
    private BooleanExpression isAfterCursor(LocalDateTime after) {
        // 'after' 커서 값보다 오래된(작은) createdAt을 가진 유저를 조회
        return user.createdAt.lt(after);
    }

    private BooleanExpression filterByPeriod(PeriodType period) {
        if (period == null || period == PeriodType.ALL_TIME) {
            // ALL_TIME이거나 period가 제공되지 않은 경우, 필터링x
            return null;
        }

        Instant startInstant;
        Instant now = Instant.now();

        switch (period) {
            case DAILY:
                startInstant = now.minus(Duration.ofDays(1));
                break;
            case WEEKLY:
                startInstant = now.minus(Duration.ofDays(7));
                break;
            case MONTHLY:
                startInstant = now.minus(Duration.ofDays(30));
                break;
            default:
                return null;
        }

        // 해당 기간(startInstant 이후)에 작성된 리뷰만 포함하도록 조건 설정
        return review.createdAt.goe(startInstant);
    }

    //물리 삭제시 N+1 문제 방지 및 JPA Cascade 작동
    public Optional<User> findByEmailWithDeleted(String email) {
        return Optional.ofNullable(queryFactory
                .selectFrom(user)
                .where(user.email.eq(email))
                .fetchOne());
    }

    //통합 테스트용
    public Optional<User> findByIdWithReviewsAndComments(UUID userId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(user)
                .leftJoin(user.reviews, review).fetchJoin()
                .leftJoin(user.comments, comment).fetchJoin()
                .where(user.id.eq(userId))
                .fetchOne());
    }
}
