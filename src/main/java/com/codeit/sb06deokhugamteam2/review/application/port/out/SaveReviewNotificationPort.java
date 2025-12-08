package com.codeit.sb06deokhugamteam2.review.application.port.out;

import com.codeit.sb06deokhugamteam2.review.domain.model.ReviewLikeNotificationDomain;

public interface SaveReviewNotificationPort {

    void sendNotification(ReviewLikeNotificationDomain notification);
}
