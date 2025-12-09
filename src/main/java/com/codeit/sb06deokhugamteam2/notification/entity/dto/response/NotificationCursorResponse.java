package com.codeit.sb06deokhugamteam2.notification.entity.dto.response;

import com.codeit.sb06deokhugamteam2.notification.entity.dto.NotificationDto;
import java.time.Instant;
import java.util.List;

public record NotificationCursorResponse(
    List<NotificationDto> content,
    String nextCursor,
    Instant nextAfter,
    Integer size,
    Long totalElements,
    Boolean hasNext
) {


}
