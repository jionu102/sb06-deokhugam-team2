package com.codeit.sb06deokhugamteam2.comment.entity;

import com.codeit.sb06deokhugamteam2.review.adapter.out.entity.Review;
import com.codeit.sb06deokhugamteam2.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "comments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false,name = "created_at", updatable = false)
    @CreatedDate
    private Instant createdAt;

    @Column(nullable = false, name = "updated_at")
    @LastModifiedDate
    private Instant updatedAt;

    private Boolean deleted = false;

    @Builder
    public Comment(User user, Review review, String content) {
        this.user = user;
        this.review = review;
        this.content = content;
    }

    public void softDelete() {
        this.deleted = true;
    }

    public void updateComment(String newContent) {
        this.content = newContent;
        this.updatedAt = Instant.now();
    }
}
