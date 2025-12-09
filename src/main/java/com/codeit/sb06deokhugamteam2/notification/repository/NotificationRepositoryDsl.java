package com.codeit.sb06deokhugamteam2.notification.repository;

import com.codeit.sb06deokhugamteam2.notification.entity.Notification;
import com.codeit.sb06deokhugamteam2.notification.entity.dto.NotificaionCursorDto;
import java.util.List;

public interface NotificationRepositoryDsl {
  List<Notification> findAllByUserId(NotificaionCursorDto dto);
}
