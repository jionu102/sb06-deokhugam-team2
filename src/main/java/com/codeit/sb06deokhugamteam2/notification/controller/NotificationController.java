package com.codeit.sb06deokhugamteam2.notification.controller;

import com.codeit.sb06deokhugamteam2.notification.entity.dto.NotificationDto;
import com.codeit.sb06deokhugamteam2.notification.entity.dto.request.NotificationUpdateRequest;
import com.codeit.sb06deokhugamteam2.notification.service.NotificationService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

  private final NotificationService notificationService;

  @PatchMapping("/{notificationId}")
  public ResponseEntity<NotificationDto> update(@PathVariable UUID notificationId,
      @RequestHeader("Deokhugam-Request-User-ID") UUID userId,
      @Valid @RequestBody NotificationUpdateRequest request) {

    return ResponseEntity.status(HttpStatus.OK).body(notificationService.updateReadState(notificationId, userId, request));
  }
}
