package com.codeit.sb06deokhugamteam2.review.infra.persistence;

import com.codeit.sb06deokhugamteam2.book.entity.Book;
import com.codeit.sb06deokhugamteam2.review.domain.ReviewDetail;
import com.codeit.sb06deokhugamteam2.review.domain.ReviewDomain;
import com.codeit.sb06deokhugamteam2.review.domain.ReviewException;
import com.codeit.sb06deokhugamteam2.review.domain.ReviewRepository;
import com.codeit.sb06deokhugamteam2.review.infra.ReviewMapper;
import com.codeit.sb06deokhugamteam2.review.infra.persistence.entity.Review;
import com.codeit.sb06deokhugamteam2.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.function.Function;

@Repository
@Transactional(readOnly = true)
public class JpaReviewRepository implements ReviewRepository {

    @PersistenceContext
    private EntityManager em;

    // Infrastructures
    private final SpringJpaReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    public JpaReviewRepository(SpringJpaReviewRepository reviewRepository, ReviewMapper reviewMapper) {
        this.reviewRepository = reviewRepository;
        this.reviewMapper = reviewMapper;
    }

    @Override
    public void existsByBookIdAndUserId(
            UUID bookId,
            UUID userId,
            Function<UUID, ? extends ReviewException> exceptionFunction
    ) {
        Book book = em.getReference(Book.class, bookId);
        User user = em.getReference(User.class, userId);
        Review query = new Review().book(book).user(user);
        reviewRepository.findOne(Example.of(query))
                .ifPresent(review -> { throw exceptionFunction.apply(bookId); });
    }

    @Override
    @Transactional
    public ReviewDetail save(ReviewDomain review) {
        ReviewDomain.Snapshot snapshot = review.createSnapshot();
        Book book = em.getReference(Book.class, review.bookId());
        User user = em.getReference(User.class, review.userId());
        Review reviewEntity = reviewMapper.toEntity(snapshot, book, user);
        em.persist(reviewEntity);
        return reviewMapper.toDomain(reviewEntity, review.likedByMe(user.getId()));
    }
}
