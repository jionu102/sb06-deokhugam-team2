package com.codeit.sb06deokhugamteam2.dashboard.repository;

import com.codeit.sb06deokhugamteam2.common.enums.PeriodType;
import com.codeit.sb06deokhugamteam2.common.enums.RankingType;
import com.codeit.sb06deokhugamteam2.dashboard.entity.QDashboard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryExtensionImpl implements UserRepositoryExtension {

    private final JPAQueryFactory queryFactory;

    @Transactional
    @Override
    public void deleteUserRankings(RankingType rankingType, PeriodType periodType) {
        QDashboard dashboard = QDashboard.dashboard;

        queryFactory.delete(dashboard)
                .where(dashboard.rankingType.eq(rankingType)
                        .and(dashboard.periodType.eq(periodType)))
                .execute();
    }
}
