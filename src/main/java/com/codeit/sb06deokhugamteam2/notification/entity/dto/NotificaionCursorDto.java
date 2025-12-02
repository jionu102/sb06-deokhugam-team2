package com.codeit.sb06deokhugamteam2.notification.entity.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record NotificaionCursorDto(
    UUID userId,
    String direction,
    String cursor,
    Instant after,
    Integer limit
) {

}
