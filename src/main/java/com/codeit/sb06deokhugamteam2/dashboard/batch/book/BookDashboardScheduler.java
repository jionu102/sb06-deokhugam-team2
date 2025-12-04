package com.codeit.sb06deokhugamteam2.dashboard.batch.book;

import com.codeit.sb06deokhugamteam2.common.enums.PeriodType;
import com.codeit.sb06deokhugamteam2.common.exception.ErrorCode;
import com.codeit.sb06deokhugamteam2.common.exception.exceptions.BookException;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class BookDashboardScheduler {

    private final JobLauncher jobLauncher;
    private final Job createRankingBooksJob;

    @Scheduled(cron = "0 0 0 * * ?")
    public void runDailyJob() {

        try {
            JobParameters params = new JobParametersBuilder()
                    .addString("periodType", PeriodType.DAILY.name()) // enum → String
                    .addLong("time", System.currentTimeMillis())      // 항상 새로운 파라미터 생성
                    .toJobParameters();

            jobLauncher.run(createRankingBooksJob, params);
        } catch (Exception e) {
            throw new BookException(
                    ErrorCode.COMMON_EXCEPTION,
                    Map.of("message", "Failed to run daily book ranking job"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Scheduled(cron = "0 1 0 * * ?")
    public void runWeeklyJob() {

        try {
            JobParameters params = new JobParametersBuilder()
                    .addString("periodType", PeriodType.WEEKLY.name())
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(createRankingBooksJob, params);
        } catch (Exception e) {
            throw new BookException(
                    ErrorCode.COMMON_EXCEPTION,
                    Map.of("message", "Failed to run weekly book ranking job"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Scheduled(cron = "0 2 0 * * ?")
    public void runMonthlyJob() {

        try {
            JobParameters params = new JobParametersBuilder()
                    .addString("periodType", PeriodType.MONTHLY.name())
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(createRankingBooksJob, params);
        } catch (Exception e) {
            throw new BookException(
                    ErrorCode.COMMON_EXCEPTION,
                    Map.of("message", "Failed to run monthly book ranking job"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Scheduled(cron = "0 3 0 * * ?")
    public void runEntireJob() {

        try {
            JobParameters params = new JobParametersBuilder()
                    .addString("periodType", PeriodType.ALL_TIME.name())
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(createRankingBooksJob, params);
        } catch (Exception e) {
            throw new BookException(
                    ErrorCode.COMMON_EXCEPTION,
                    Map.of("message", "Failed to run entire book ranking job"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
