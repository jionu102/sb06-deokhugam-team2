package com.codeit.sb06deokhugamteam2.notification.repository;

import com.codeit.sb06deokhugamteam2.notification.entity.Notification;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, UUID>, NotificationRepositoryDsl {

  Optional<Notification> findByIdAndUserId(UUID notificationId, UUID userId);
  Optional<List<Notification>> findByUserId(UUID userId);
  Long countByUserId(UUID userId);
}
