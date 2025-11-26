package com.codeit.sb06deokhugamteam2.review.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "Reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @Column(name = "rating", nullable = false)
    private Integer rating;

    @NotNull
    @Column(name = "content", nullable = false, length = Integer.MAX_VALUE)
    private String content;

    @NotNull
    @Column(name = "like_count", nullable = false)
    private Integer likeCount;

    @NotNull
    @Column(name = "comment_count", nullable = false)
    private Integer commentCount;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    public Review id(UUID id) {
        this.id = id;
        return this;
    }

    public Review rating(Integer rating) {
        this.rating = rating;
        return this;
    }

    public Review content(String content) {
        this.content = content;
        return this;
    }

    public Review likeCount(Integer likeCount) {
        this.likeCount = likeCount;
        return this;
    }

    public Review commentCount(Integer commentCount) {
        this.commentCount = commentCount;
        return this;
    }

    public Review createdAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Review updatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public Review deleted(Boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    public UUID id() {
        return id;
    }

    public Integer rating() {
        return rating;
    }

    public String content() {
        return content;
    }

    public Integer likeCount() {
        return likeCount;
    }

    public Integer commentCount() {
        return commentCount;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public Boolean deleted() {
        return deleted;
    }
}
