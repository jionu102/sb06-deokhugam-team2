package com.codeit.sb06deokhugamteam2.user.entity;

//import com.codeit.sb06deokhugamteam2.comment.entity.Comment;
//import com.codeit.sb06deokhugamteam2.review.infra.persistence.entity.Review;
//import com.codeit.sb06deokhugamteam2.review.infra.persistence.entity.ReviewLike;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
//import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
//import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "users")
@SQLDelete(sql = """
        UPDATE users
        SET deleted_at = current_timestamp()
        WHERE id = ?
        """)
@SQLRestriction("deleted_at IS NULL")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @Builder.Default
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "deleted_at") // 논리 삭제된 시간 필드 추가
    private LocalDateTime deletedAt;

//    @Builder.Default
//    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
//    private List<Review> reviews = new ArrayList<>();
//
//    @Builder.Default
//    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
//    private List<Comment> comments = new ArrayList<>();
//
//    @Builder.Default
//    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
//    private List<ReviewLike> reviewLikes = new ArrayList<>();

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
}