package com.codeit.sb06deokhugamteam2.notification;

import com.codeit.sb06deokhugamteam2.common.exception.ErrorCode;
import com.codeit.sb06deokhugamteam2.common.exception.exceptions.NotificationException;
import com.codeit.sb06deokhugamteam2.notification.entity.Notification;
import com.codeit.sb06deokhugamteam2.notification.entity.dto.NotificationDto;
import com.codeit.sb06deokhugamteam2.notification.entity.dto.request.NotificationCreateRequest;
import com.codeit.sb06deokhugamteam2.notification.repository.NotificationRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class NotificationComponent {
  private final NotificationRepository repository;

  public NotificationDto saveNotification(@NotNull NotificationCreateRequest request) {

    try
    {
      if(request == null)
        throw new NotificationException(ErrorCode.EMPTY_DATA, Map.of(), HttpStatus.BAD_REQUEST);

      Notification notification = new Notification();
      notification.setUserId(request.userId());
      notification.setReviewId(request.reviewId());
      notification.setReviewTitle(request.reviewTitle());
      notification.setContent(request.content());

      Notification saved = repository.save(notification);
      NotificationDto notificationDto = new NotificationDto(saved);
      return notificationDto;
    }
    catch (Exception e)
    {
      throw new NotificationException(ErrorCode.COMMON_EXCEPTION, Map.of("error", e.getMessage()),  HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
