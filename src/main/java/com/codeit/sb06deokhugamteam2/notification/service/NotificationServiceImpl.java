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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService{
  private final NotificationRepository repository;


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
}
