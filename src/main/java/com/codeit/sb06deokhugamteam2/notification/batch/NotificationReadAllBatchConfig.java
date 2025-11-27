package com.codeit.sb06deokhugamteam2.notification.batch;

import com.codeit.sb06deokhugamteam2.notification.entity.Notification;
import com.codeit.sb06deokhugamteam2.notification.repository.NotificationRepository;
import jakarta.persistence.EntityManagerFactory;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class NotificationReadAllBatchConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final EntityManagerFactory entityManagerFactory;
  private final NotificationRepository notificationRepository;

  @Bean
  public Job readAllNoitificationsJob() {
    return new JobBuilder("readAllNoitificationsJob",jobRepository)
        .start(readAllNoitificationsStep())
        .build();
  }

  @Bean
  public Step readAllNoitificationsStep() {
    return new StepBuilder("readAllNoitificationsStep", jobRepository)
        .<Notification, Notification>chunk(100, transactionManager)
        .reader(readAllNoitificationsItemReader(null))
        .processor(readAllNoitificationsItemProcessor())
        .writer(writeAllNoitificationsItemWriter())
        .build();
  }

  @Bean
  @StepScope
  public JpaPagingItemReader<Notification> readAllNoitificationsItemReader(
      @Value("#{jobParameters['userId']}") String userId
  ) {
    return new JpaPagingItemReaderBuilder<Notification>()
        .name("readAllNoitificationsItemReader")
        .entityManagerFactory(entityManagerFactory)
        .queryString(
            "SELECT n FROM Notification n "
                + "WHERE n.userId = :userId AND "
                + " n.createdAt = n.confirmedAt")
        .parameterValues(Map.of("userId", UUID.fromString(userId)))
        .pageSize(100)
        .build();
  }

  @Bean
  public ItemProcessor<Notification, Notification> readAllNoitificationsItemProcessor() {
    return notification -> {
      notification.setConfirmedAt(Instant.now());
      return notification;
    };
  }

  @Bean
  public ItemWriter<Notification> writeAllNoitificationsItemWriter() {
    return notifications -> notificationRepository.saveAll(notifications);
  }
}
