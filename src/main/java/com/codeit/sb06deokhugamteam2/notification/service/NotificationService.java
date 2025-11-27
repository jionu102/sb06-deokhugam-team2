package com.codeit.sb06deokhugamteam2.notification.service;

import com.codeit.sb06deokhugamteam2.notification.entity.dto.NotificationDto;
import com.codeit.sb06deokhugamteam2.notification.entity.dto.request.NotificationUpdateRequest;
import java.util.UUID;

public interface NotificationService {

  NotificationDto updateReadState(UUID notificationId, UUID userId, NotificationUpdateRequest request);
}
