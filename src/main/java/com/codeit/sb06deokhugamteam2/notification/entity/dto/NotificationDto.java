package com.codeit.sb06deokhugamteam2.notification.entity.dto;

import com.codeit.sb06deokhugamteam2.notification.entity.Notification;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationDto {
  UUID id;
  UUID userId;
  UUID reviewId;
  String reviewTitle;
  String content;
  Boolean confirmed;
  Instant createdAt;
  Instant updatedAt;

  public NotificationDto(Notification notification) {
    this.id = notification.getId();
    this.userId = notification.getUserId();
    this.reviewId = notification.getReviewId();
    this.reviewTitle = notification.getReviewTitle();
    this.content = notification.getContent();
    this.createdAt = notification.getCreatedAt();
    this.updatedAt = notification.getConfirmedAt();
    this.confirmed = false;

    if(this.createdAt.isBefore(this.updatedAt))
      this.confirmed = true;
  }
}
