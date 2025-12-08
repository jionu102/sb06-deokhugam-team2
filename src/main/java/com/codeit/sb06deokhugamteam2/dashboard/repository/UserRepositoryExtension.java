package com.codeit.sb06deokhugamteam2.dashboard.repository;

import com.codeit.sb06deokhugamteam2.common.enums.PeriodType;
import com.codeit.sb06deokhugamteam2.common.enums.RankingType;

public interface UserRepositoryExtension {

    void deleteUserRankings(RankingType rankingType, PeriodType periodType);
}