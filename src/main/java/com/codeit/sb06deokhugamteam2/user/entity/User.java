package com.codeit.sb06deokhugamteam2.user.entity;


import com.codeit.sb06deokhugamteam2.comment.entity.Comment;
import com.codeit.sb06deokhugamteam2.like.adapter.out.entity.ReviewLike;
import com.codeit.sb06deokhugamteam2.review.adapter.out.entity.Review;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "users")
@SQLDelete(sql = """
        UPDATE users
        SET deleted_at = current_timestamp
        WHERE id = ?
        """)
@SQLRestriction("deleted_at IS NULL")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"reviews", "comments", "reviewLikes"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "nickname", nullable = false, length = 20)
    private String nickname;

    @Column(name = "password", nullable = false)
    private String password;

    @CreationTimestamp  // JPA 생명주기 활용
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at") // 논리 삭제된 시간 필드 추가
    private LocalDateTime deletedAt;

    @Builder.Default
    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<ReviewLike> reviewLikes = new ArrayList<>();

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
}