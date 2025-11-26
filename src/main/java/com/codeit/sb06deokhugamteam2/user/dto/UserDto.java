package com.codeit.sb06deokhugamteam2.user.dto;

import java.time.LocalDateTime;
import java.util.UUID;


public record UserDto(
        UUID id,
        String email,
        String nickname,
        LocalDateTime createdAt
) {
}
