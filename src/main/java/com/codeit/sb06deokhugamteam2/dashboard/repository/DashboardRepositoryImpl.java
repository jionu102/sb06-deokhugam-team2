package com.codeit.sb06deokhugamteam2.dashboard.repository;

import com.codeit.sb06deokhugamteam2.common.enums.PeriodType;
import com.codeit.sb06deokhugamteam2.common.enums.RankingType;
import com.codeit.sb06deokhugamteam2.dashboard.entity.Dashboard;
import com.codeit.sb06deokhugamteam2.dashboard.entity.QDashboard;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

@RequiredArgsConstructor
public class DashboardRepositoryImpl implements DashboardRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Dashboard> findPopularBookListByCursor(
            RankingType rankingType,
            PeriodType period,
            String cursor,
            Instant after,
            Sort.Direction direction,
            Integer limit
    ) {
        QDashboard dashboard = QDashboard.dashboard;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(dashboard.rankingType.eq(rankingType));
        builder.and(dashboard.periodType.eq(period));

        // 오늘 생성된 데이터만 조회 -- 00:00 기준
        Instant today = LocalDate.now()
                .atStartOfDay(ZoneId.of("UTC"))
                .toInstant();

        builder.and(dashboard.createdAt.goe(today));

        if (cursor != null && after != null) {
            if (direction == Sort.Direction.ASC) {
                builder.and(
                        dashboard.rank.gt(Long.parseLong(cursor))
                );

            } else {
                builder.and(
                        dashboard.rank.lt(Long.parseLong(cursor))
                );
            }
        }

        return queryFactory
                .selectFrom(dashboard)
                .where(builder)
                .orderBy(direction == Sort.Direction.ASC ?
                                dashboard.rank.asc() :      // 1등부터
                                dashboard.rank.desc(),      // 꼴등부터
                        direction == Sort.Direction.ASC ?
                                dashboard.createdAt.asc() :      // 오래된 순
                                dashboard.createdAt.desc()       // 최신 순
                ).limit(limit + 1)
                .fetch();
    }
}
