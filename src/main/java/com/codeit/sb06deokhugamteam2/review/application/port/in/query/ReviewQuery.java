package com.codeit.sb06deokhugamteam2.review.application.port.in.query;

import java.util.UUID;

public record ReviewQuery(UUID reviewId, UUID requestUserId) {
}
