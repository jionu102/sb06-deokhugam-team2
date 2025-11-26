package com.codeit.sb06deokhugamteam2.notification.entity.dto.request;

import jakarta.validation.constraints.NotNull;

public record NotificationUpdateRequest(
    @NotNull Boolean confirmed
) {

}
