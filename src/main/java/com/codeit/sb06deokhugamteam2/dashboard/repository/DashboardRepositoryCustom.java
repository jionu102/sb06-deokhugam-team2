package com.codeit.sb06deokhugamteam2.dashboard.repository;

import com.codeit.sb06deokhugamteam2.dashboard.entity.Dashboard;
import com.codeit.sb06deokhugamteam2.common.enums.PeriodType;
import com.codeit.sb06deokhugamteam2.common.enums.RankingType;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.List;

public interface DashboardRepositoryCustom {

    List<Dashboard> findPopularBookListByCursor(
            RankingType rankingType,
            PeriodType period,
            String cursor,
            Instant after,
            Sort.Direction direction,
            Integer limit
    );
}
