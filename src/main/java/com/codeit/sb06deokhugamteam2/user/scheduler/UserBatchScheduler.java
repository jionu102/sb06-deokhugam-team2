package com.codeit.sb06deokhugamteam2.user.scheduler;

import com.codeit.sb06deokhugamteam2.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserBatchScheduler {

    private final UserService userService;

    @Scheduled(cron = "0 0 3 * * *") // 매일 3시 0분 0초에 실행
    public void hardDeleteOldUsersJob() {
        log.info("Batch Job Start: Starting hard deletion of old soft-deleted users.");

        // 24.0 hoursAgo: 24시간 이상 경과한 유저를 대상으로 실행
        int deletedCount = userService.batchHardDeleteOldSoftDeletedUsers(24.0);

        log.info("Batch Job Finish: {} users hard deleted.", deletedCount);
    }
}