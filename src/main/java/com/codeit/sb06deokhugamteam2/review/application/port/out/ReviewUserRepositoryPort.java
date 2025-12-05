package com.codeit.sb06deokhugamteam2.review.application.port.out;

import java.util.UUID;

public interface ReviewUserRepositoryPort {

    boolean existsById(UUID userId);
}
