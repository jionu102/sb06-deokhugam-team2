package com.codeit.sb06deokhugamteam2.notification.repository;

import com.codeit.sb06deokhugamteam2.notification.entity.Notification;
import com.codeit.sb06deokhugamteam2.notification.entity.dto.NotificaionCursorDto;
import com.codeit.sb06deokhugamteam2.notification.entity.dto.response.NotificationCursorResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface NotificationRepositoryDsl {
  List<Notification> findAllByUserId(NotificaionCursorDto dto);
}
