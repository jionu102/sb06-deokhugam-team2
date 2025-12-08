package com.codeit.sb06deokhugamteam2.review.adapter.out.repository;

import com.codeit.sb06deokhugamteam2.notification.entity.Notification;
import com.codeit.sb06deokhugamteam2.review.application.port.out.SaveReviewNotificationPort;
import com.codeit.sb06deokhugamteam2.review.domain.model.ReviewLikeNotificationDomain;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class ReviewNotificationRepository implements SaveReviewNotificationPort {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void sendNotification(ReviewLikeNotificationDomain notification) {
        var notificationEntity = new Notification();
        notificationEntity.setUserId(notification.userId());
        notificationEntity.setReviewId(notification.reviewId());
        notificationEntity.setReviewTitle(notification.reviewTitle());
        notificationEntity.setContent(notification.content());
        em.persist(notificationEntity);
    }
}
