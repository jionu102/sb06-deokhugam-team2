package com.codeit.sb06deokhugamteam2.dashboard.batch.book;

import com.codeit.sb06deokhugamteam2.common.enums.PeriodType;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

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
            throw new RuntimeException();   // todo 예외처리
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
            throw new RuntimeException();
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
            throw new RuntimeException();
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
            throw new RuntimeException();
        }
    }
}
