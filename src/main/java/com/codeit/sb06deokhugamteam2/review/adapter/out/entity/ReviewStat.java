package com.codeit.sb06deokhugamteam2.review.adapter.out.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
@Table(name = "review_stats")
public class ReviewStat {

    @Id
    @Column(name = "review_id", nullable = false)
    private UUID id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @NotNull
    @Column(name = "like_count", nullable = false)
    private Integer likeCount;

    @NotNull
    @Column(name = "comment_count", nullable = false)
    private Integer commentCount;

    public ReviewStat id(UUID id) {
        this.id = id;
        return this;
    }

    public ReviewStat review(Review review) {
        this.review = review;
        return this;
    }

    public ReviewStat likeCount(Integer likeCount) {
        this.likeCount = likeCount;
        return this;
    }

    public ReviewStat commentCount(Integer commentCount) {
        this.commentCount = commentCount;
        return this;
    }

    public UUID id() {
        return id;
    }

    public Review review() {
        return review;
    }

    public Integer likeCount() {
        return likeCount;
    }

    public Integer commentCount() {
        return commentCount;
    }
}
