package com.codeit.sb06deokhugamteam2.notification.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "notifications")
public class Notification {

  @Id
  @UuidGenerator
  @Column(name = "id", nullable = false)
  private UUID id;

  @NotNull
  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @NotNull
  @Column(name = "review_id", nullable = false)
  private UUID reviewId;

  @NotNull
  @Column(name = "review_title", nullable = false, length = Integer.MAX_VALUE)
  private String reviewTitle;

  @NotNull
  @Column(name = "content", nullable = false, length = Integer.MAX_VALUE)
  private String content;

  @NotNull
  @LastModifiedDate
  @Column(name = "confirmed_at", nullable = false)
  private Instant confirmedAt;

  @NotNull
  @CreatedDate
  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

}