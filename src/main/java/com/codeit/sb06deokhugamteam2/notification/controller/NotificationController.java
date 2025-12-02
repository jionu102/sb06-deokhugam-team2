package com.codeit.sb06deokhugamteam2.notification.controller;

import com.codeit.sb06deokhugamteam2.notification.entity.dto.NotificaionCursorDto;
import com.codeit.sb06deokhugamteam2.notification.entity.dto.NotificationDto;
import com.codeit.sb06deokhugamteam2.notification.entity.dto.request.NotificationUpdateRequest;
import com.codeit.sb06deokhugamteam2.notification.entity.dto.response.NotificationCursorResponse;
import com.codeit.sb06deokhugamteam2.notification.service.NotificationService;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

  @PatchMapping("/read-all")
  public ResponseEntity<Void> updateAll(@RequestHeader("Deokhugam-Request-User-ID") UUID userId)
  {
    notificationService.updateAllReadState(userId);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @GetMapping
  public ResponseEntity<NotificationCursorResponse> getAllNotifications(@RequestParam UUID userId,
      @RequestParam(defaultValue = "DESC", required = false) String direction,
      @RequestParam(defaultValue = "", required = false) String cursor,
      @RequestParam(required = false) Instant after,
      @RequestParam(defaultValue = "20", required = false) Integer limit
      ) {

    NotificaionCursorDto dto = NotificaionCursorDto.builder()
        .userId(userId)
        .direction(direction)
        .cursor(cursor)
        .after(after == null? Instant.now():after)
        .limit(limit)
        .build();

    return ResponseEntity.status(HttpStatus.OK).body(notificationService.getUserNotifications(dto));
  }
}
