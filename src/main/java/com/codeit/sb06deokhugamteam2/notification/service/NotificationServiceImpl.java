package com.codeit.sb06deokhugamteam2.notification.service;

import com.codeit.sb06deokhugamteam2.common.exception.ErrorCode;
import com.codeit.sb06deokhugamteam2.common.exception.exceptions.NotificationException;
import com.codeit.sb06deokhugamteam2.notification.entity.Notification;
import com.codeit.sb06deokhugamteam2.notification.entity.dto.NotificationDto;
import com.codeit.sb06deokhugamteam2.notification.entity.dto.request.NotificationUpdateRequest;
import com.codeit.sb06deokhugamteam2.notification.repository.NotificationRepository;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService{
  private final NotificationRepository repository;
  private final JobLauncher jobLauncher;
  private final Job readAllNoitificationsJob;


  @Override
  public NotificationDto updateReadState(UUID notificationId, UUID userId, NotificationUpdateRequest request) {
    Notification notification = repository.findByIdAndUserId(notificationId, userId)
        .orElseThrow(() -> new NotificationException(ErrorCode.INVALID_DATA, Map.of("notificationId",notificationId,"userId", userId),
        HttpStatus.NOT_FOUND));

    Notification saved = notification;
    if(request.confirmed())
    {
      notification.setConfirmedAt(Instant.now());
      saved = repository.save(notification);
    }

    NotificationDto dto = new NotificationDto(saved);
    return dto;
  }

  @Override
  public void updateAllReadState(UUID userId) {
    try
    {
      JobParameters jobParameters = new JobParametersBuilder()
          .addLong("time",System.currentTimeMillis())
          .addString("userId",userId.toString())
          .toJobParameters();

      jobLauncher.run(readAllNoitificationsJob,jobParameters);
    }
    catch (Exception e)
    {
      throw new NotificationException(ErrorCode.COMMON_EXCEPTION,Map.of("message",e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
