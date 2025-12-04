package com.codeit.sb06deokhugamteam2.review.domain.port;

import java.util.UUID;

public interface ReviewUserRepository {

    boolean existsById(UUID userId);
}
