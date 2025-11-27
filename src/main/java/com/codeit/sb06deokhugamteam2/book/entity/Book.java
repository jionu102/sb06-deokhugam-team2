package com.codeit.sb06deokhugamteam2.book.entity;

import com.codeit.sb06deokhugamteam2.review.entity.Review;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "books")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String publisher;

    @Column(name = "published_date", nullable = false)
    private LocalDate publishedDate;

    @Column(nullable = true)
    private String isbn;

    @Column(nullable = true, name = "thumbnail_url")
    private String thumbnailUrl;

    @Builder.Default    // 빌더 사용 시 기본값 설정
    @Column(nullable = false, name = "review_count")
    private Integer reviewCount = 0;

    @Builder.Default
    @Column(nullable = false, name = "rating_sum")
    private double ratingSum = 0.0;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 100)
    private List<Review> reviews = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, name = "created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false, name = "updated_at")
    private Instant updatedAt;

    @Builder.Default
    @Column(nullable = false)
    private boolean deleted = false;

    public void update(String thumbnailUrl) {
        if (thumbnailUrl != null) {
            this.thumbnailUrl = thumbnailUrl;
        }
    }

    public void setDeletedAsTrue() {
        this.deleted = true;
    }
}
