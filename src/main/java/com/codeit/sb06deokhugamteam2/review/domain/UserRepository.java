package com.codeit.sb06deokhugamteam2.review.domain;

import java.util.UUID;
import java.util.function.Function;

public interface UserRepository {

    void exists(UUID userId, Function<UUID, ? extends ReviewException> exceptionFunction);
}
