package com.codeit.sb06deokhugamteam2.review.application.port.out;

import java.util.UUID;

public interface LoadReviewUserPort {

    boolean existsById(UUID userId);
}
