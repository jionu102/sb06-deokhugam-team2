package com.codeit.sb06deokhugamteam2.review.adapter.out.entity;

import com.codeit.sb06deokhugamteam2.book.entity.Book;
import com.codeit.sb06deokhugamteam2.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "Reviews", uniqueConstraints = {
        @UniqueConstraint(name = "reviews_pk", columnNames = {"book_id", "user_id"})
})
public class Review {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "review")
    private Set<ReviewLike> likes = new HashSet<>();

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

    public Review book(Book book) {
        this.book = book;
        return this;
    }

    public Review user(User user) {
        this.user = user;
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

    public Review likes(Set<ReviewLike> likes) {
        this.likes = likes;
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

    public Book book() {
        return book;
    }

    public User user() {
        return user;
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

    public Set<ReviewLike> likes() {
        return likes;
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
