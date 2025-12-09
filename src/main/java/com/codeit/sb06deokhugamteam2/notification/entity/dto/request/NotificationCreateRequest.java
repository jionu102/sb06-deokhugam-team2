package com.codeit.sb06deokhugamteam2.notification.entity.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record NotificationCreateRequest(

    @NotNull UUID userId,
    @NotNull UUID reviewId,
    @NotEmpty String reviewTitle,
    @NotEmpty String content
) {

}
