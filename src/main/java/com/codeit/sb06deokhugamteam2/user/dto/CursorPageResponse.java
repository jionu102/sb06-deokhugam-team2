package com.codeit.sb06deokhugamteam2.user.dto;

import java.time.LocalDateTime;
import java.util.List;


public record CursorPageResponse<T>(
        List<T> content,
        String nextCursor,
        LocalDateTime nextAfter,
        int size,
        long totalElements,
        boolean hasNext
) {
}
