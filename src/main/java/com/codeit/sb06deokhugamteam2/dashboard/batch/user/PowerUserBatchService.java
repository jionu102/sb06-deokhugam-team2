package com.codeit.sb06deokhugamteam2.dashboard.batch.user;

import com.codeit.sb06deokhugamteam2.common.enums.PeriodType;
import com.codeit.sb06deokhugamteam2.common.enums.RankingType;
import com.codeit.sb06deokhugamteam2.dashboard.repository.UserRepositoryExtension;
import com.codeit.sb06deokhugamteam2.user.repository.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PowerUserBatchService {

    private final UserQueryRepository userQueryRepository;
    private final UserRepositoryExtension userRepositoryExtension;

    private void processRankingForPeriod(PeriodType periodType) {
        log.info("  🔍 Start calculating for period: {}", periodType);

        userRepositoryExtension.deleteUserRankings(RankingType.USER, periodType);
        log.info("  🗑️ Deleted existing {} rankings for period: {}", RankingType.USER, periodType);

    }
}