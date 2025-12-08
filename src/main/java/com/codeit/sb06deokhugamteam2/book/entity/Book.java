package com.codeit.sb06deokhugamteam2.book.entity;

import com.codeit.sb06deokhugamteam2.review.adapter.out.entity.Review;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SQLRestriction;
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
@SQLRestriction("deleted = false")
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

    @OneToOne(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter
    private BookStats bookStats;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

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

    @Builder.Default
    @Column(nullable = false)
    private Boolean deleted = false;

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
