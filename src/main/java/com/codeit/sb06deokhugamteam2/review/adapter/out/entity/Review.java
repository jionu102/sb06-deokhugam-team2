package com.codeit.sb06deokhugamteam2.review.adapter.out.entity;

import com.codeit.sb06deokhugamteam2.book.entity.Book;
import com.codeit.sb06deokhugamteam2.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.SoftDelete;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@SoftDelete
@Table(name = "reviews")
public class Review {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "review",
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            orphanRemoval = true
    )
    private Set<ReviewLike> likes = new HashSet<>();

    @NotNull
    @OneToOne(
            mappedBy = "review",
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            orphanRemoval = true
    )
    private ReviewStat reviewStat;

    @NotNull
    @Column(name = "rating", nullable = false)
    private Integer rating;

    @NotNull
    @Column(name = "content", nullable = false, length = 500)
    private String content;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

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

    public Review addLike(ReviewLike reviewLike) {
        this.likes.add(reviewLike);
        reviewLike.review(this);
        return this;
    }

    public Review removeLike(ReviewLike reviewLike) {
        this.likes.remove(reviewLike);
        reviewLike.review(null);
        return this;
    }

    public Review reviewStat(ReviewStat reviewStat) {
        this.reviewStat = reviewStat;
        reviewStat.review(this);
        return this;
    }

    public Review removeReviewStat(ReviewStat reviewStat) {
        this.reviewStat = null;
        reviewStat.review(null);
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

    public Review createdAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Review updatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
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

    public Set<ReviewLike> likes() {
        return likes;
    }

    public ReviewStat reviewStat() {
        return reviewStat;
    }

    public Integer rating() {
        return rating;
    }

    public String content() {
        return content;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
