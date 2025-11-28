package com.codeit.sb06deokhugamteam2.notification.batch;

import com.codeit.sb06deokhugamteam2.notification.entity.Notification;
import com.codeit.sb06deokhugamteam2.notification.repository.NotificationRepository;
import jakarta.persistence.EntityManagerFactory;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class NotificationDeleteBatchConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final EntityManagerFactory entityManagerFactory;
  private final NotificationRepository notificationRepository;

  @Bean
  public Job deleteAllNotificationsJob() {
    return new JobBuilder("deleteAllNotificationsJob",jobRepository)
        .start(deleteAllNotificationsStep())
        .build();
  }

  @Bean
  public Step deleteAllNotificationsStep() {
    return new StepBuilder("deleteAllNotificationsStep", jobRepository)
        .<Notification, Notification>chunk(100, transactionManager)
        .reader(deleteAllNotificationsItemReader())
        .writer(deleteAllNotificationWriter())
        .build();
  }

  @Bean
  @StepScope
  public JpaPagingItemReader<Notification> deleteAllNotificationsItemReader()
  {
    Instant oneWeek = Instant.now().minus(7, ChronoUnit.DAYS);
    return new JpaPagingItemReaderBuilder<Notification>()
        .name("deleteAllNotificationsItemReader")
        .entityManagerFactory(entityManagerFactory)
        .queryString(
            "SELECT n FROM Notification n " +
                "WHERE n.createdAt !=  n.confirmedAt " +
                "AND n.createdAt < :oneWeekAgo")
        .parameterValues(Map.of("oneWeekAgo", oneWeek))
        .pageSize(100)
        .build();
  }

  @Bean
  public ItemWriter<Notification> deleteAllNotificationWriter()
  {
    return notifications -> notificationRepository.deleteAll(notifications);
  }
}
