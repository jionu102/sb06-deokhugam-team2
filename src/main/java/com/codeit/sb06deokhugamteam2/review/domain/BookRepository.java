package com.codeit.sb06deokhugamteam2.review.domain;

import java.util.UUID;
import java.util.function.Function;

public interface BookRepository {

    void exists(UUID bookId, Function<UUID, ? extends ReviewException> exceptionFunction);
}
