package com.codeit.sb06deokhugamteam2.dashboard.batch.book;


import com.codeit.sb06deokhugamteam2.book.entity.Book;
import com.codeit.sb06deokhugamteam2.common.enums.PeriodType;
import com.codeit.sb06deokhugamteam2.common.enums.RankingType;
import com.codeit.sb06deokhugamteam2.dashboard.entity.Dashboard;
import com.codeit.sb06deokhugamteam2.dashboard.repository.DashboardRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Configuration
@RequiredArgsConstructor
public class BookDashboardCreateBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final DashboardRepository dashboardRepository;



    @Bean
    public Job createRankingBooksJob() {
        return new JobBuilder("createRankingBooksJob", jobRepository)
                .start(createDailyRankingBooksStep())
                .build();
    }

    @Bean
    public Step createDailyRankingBooksStep() {
        return new StepBuilder("createDailyRankingBooksStep", jobRepository)
                .<Book, Dashboard>chunk(100, transactionManager)
                .reader(createRankingBooksItemReader(null))
                .processor(createRankingBooksItemProcessor(null))
                .writer(createRankingBooksWriter())
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<Book> createRankingBooksItemReader(
            @Value("#{jobParameters['periodType']}") PeriodType periodType
    ) {

        Instant since = null;

        switch (periodType) {
            case DAILY -> since = Instant.now().minus(1, ChronoUnit.DAYS);
            case WEEKLY -> since = Instant.now().minus(7, ChronoUnit.DAYS);
            case MONTHLY -> since = Instant.now().minus(30, ChronoUnit.DAYS);
            case ALL_TIME -> {}
        }

        /*
         1. 계산된 점수 기준 내림차순 정렬
         2. 점수가 같을 경우 도서의 생성일 기준 내림차순 정렬
         3. 정렬 순서대로 랭크 부여 예정
         4. 현재 review의 createdAt은 반영하지 않음 (추후 반영 고려)
         */
        return new JpaPagingItemReaderBuilder<Book>()
                .name("createRankingBooksItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString(
                        " SELECT b " +
                                " FROM Book b " +
                                " WHERE b.reviewCount > 0 " +
                                " AND (:since IS NULL OR b.createdAt >= :since) " +
                                " ORDER BY (b.reviewCount * 0.4 + b.ratingSum / b.reviewCount * 0.6) DESC, b.createdAt DESC ")
                .parameterValues(Map.of("since", since))
                .pageSize(100)
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<Book, Dashboard> createRankingBooksItemProcessor(
            @Value("#{jobParameters['periodType']}") PeriodType periodType
    ) {
        /*
         1. 정렬된 순서대로 랭크 부여
         2. 1등이 제일 먼저 만들어져야 함 (보조커서 after 처리를 위해)
         */
        AtomicLong rank = new AtomicLong(1L);
        return book ->
                Dashboard.builder()
                        .entityId(book.getId())
                        .rankingType(RankingType.BOOK)
                        .periodType(periodType)
                        .rank(rank.getAndIncrement())
                        .build();
    }

    @Bean
    public ItemWriter<Dashboard> createRankingBooksWriter() {
        return dashboards -> dashboardRepository.saveAll(dashboards);
    }
}
