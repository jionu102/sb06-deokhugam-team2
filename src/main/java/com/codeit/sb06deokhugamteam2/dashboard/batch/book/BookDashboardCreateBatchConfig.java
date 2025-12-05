package com.codeit.sb06deokhugamteam2.dashboard.batch.book;


import com.codeit.sb06deokhugamteam2.book.dto.data.BookScoreDto;
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
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Configuration
@RequiredArgsConstructor
public class BookDashboardCreateBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final DataSource dataSource;



    @Bean
    public Job createRankingBooksJob() {
        return new JobBuilder("createRankingBooksJob", jobRepository)
                .start(createDailyRankingBooksStep())
                .build();
    }

    @Bean
    public Step createDailyRankingBooksStep() {
        return new StepBuilder("createDailyRankingBooksStep", jobRepository)
                .<BookScoreDto, Dashboard>chunk(100, transactionManager)
                .reader(createRankingBooksItemReader(null))
                .processor(createRankingBooksItemProcessor(null))
                .writer(createRankingBooksWriter())
                .build();
    }

    @Bean
    @StepScope
    public JdbcCursorItemReader<BookScoreDto> createRankingBooksItemReader(
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
         3. since 가 null 이면 전체 기간, 아니면 해당 기간 내의 리뷰만 집계
         */
        String nativeQuery =
                "SELECT b.id, b.created_at, COUNT(r.book_id) AS review_count, SUM(r.rating) AS rating_sum, " +
                        "       (COUNT(r.book_id) * 0.4 + SUM(r.rating) / COUNT(r.book_id) * 0.6) AS score " +
                        "FROM books as b " +
                        "LEFT JOIN review as r ON b.id = r.book_id " +
                        "AND ( ? IS NULL OR r.created_at >= ? ) " +
                        "GROUP BY b.id, b.created_at " +
                        "HAVING COUNT(r.book_id) > 0 " +
                        "ORDER BY score DESC, b.created_at DESC";

        final Instant finalSince = since;

        return new JdbcCursorItemReaderBuilder<BookScoreDto>()
                .name("createRankingBooksItemReader")
                .dataSource(dataSource)
                .sql(nativeQuery)
                .preparedStatementSetter(ps -> {
                    if (finalSince != null) {
                        ps.setTimestamp(1, Timestamp.from(finalSince));
                        ps.setTimestamp(2, Timestamp.from(finalSince));
                    } else {
                        ps.setNull(1, Types.TIMESTAMP);
                        ps.setNull(2, Types.TIMESTAMP);
                    }
                })
                .rowMapper((rs, rowNum) -> {
                    UUID id = UUID.fromString(rs.getString("id"));
                    long reviewCount = rs.getLong("review_count");
                    double rating = rs.getDouble("rating_sum") / reviewCount;
                    return BookScoreDto.builder()
                            .id(id)
                            .createdAt(rs.getTimestamp("created_at").toInstant())
                            .periodReviewCount(reviewCount)
                            .periodRating(rating)
                            .build();
                })
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<BookScoreDto, Dashboard> createRankingBooksItemProcessor(
            @Value("#{jobParameters['periodType']}") PeriodType periodType
    ) {
        /*
         1. 정렬된 순서대로 랭크 부여
         2. 1등이 제일 먼저 만들어져야 함 (보조커서 after 처리를 위해)
         */
        AtomicLong rank = new AtomicLong(1L);
        return bookScoreDto ->
                Dashboard.builder()
                        .entityId(bookScoreDto.id())
                        .rankingType(RankingType.BOOK)
                        .periodType(periodType)
                        .rank(rank.getAndIncrement())
                        .build();
    }

    @Bean
    public JpaItemWriter<Dashboard> createRankingBooksWriter() {
        // 영속성 컨텍스트에 있는 엔티티들을 데이터베이스에 저장
        return new JpaItemWriterBuilder<Dashboard>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}
