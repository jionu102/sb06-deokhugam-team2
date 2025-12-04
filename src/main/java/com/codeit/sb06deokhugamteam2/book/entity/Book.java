package com.codeit.sb06deokhugamteam2.book.entity;

import com.codeit.sb06deokhugamteam2.review.adapter.out.entity.Review;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SoftDelete;
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
@SoftDelete
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

    @Setter
    @Builder.Default    // 빌더 사용 시 기본값 설정
    @Column(nullable = false, name = "review_count")
    private int reviewCount = 0;

    @Setter
    @Builder.Default
    @Column(nullable = false, name = "rating_sum")
    private int ratingSum = 0;

    @Builder.Default
    @OneToMany(
            mappedBy = "book",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @BatchSize(size = 100)
    private List<Review> reviews = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, name = "created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false, name = "updated_at")
    private Instant updatedAt;

    public void updateThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public void updateAll(String title, String author, String description, String publisher, LocalDate publishedDate, String thumbnailUrl) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.thumbnailUrl = thumbnailUrl;
    }
}
