package com.codeit.sb06deokhugamteam2.review.adapter.out.repository;

import com.codeit.sb06deokhugamteam2.review.application.port.out.LoadReviewUserPort;
import com.codeit.sb06deokhugamteam2.review.application.port.out.SaveReviewUserPort;
import com.codeit.sb06deokhugamteam2.review.domain.model.ReviewUserDomain;
import com.codeit.sb06deokhugamteam2.user.entity.User;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

import static com.codeit.sb06deokhugamteam2.user.entity.QUser.user;

@Repository
public class ReviewUserJpaRepository implements LoadReviewUserPort, SaveReviewUserPort {

    @PersistenceContext
    private EntityManager em;

    private <T> JPAQuery<T> select(Expression<T> expr) {
        return new JPAQuery<>(em).select(expr);
    }

    @Override
    public boolean existsById(UUID userId) {
        Integer userExists = select(Expressions.ONE)
                .from(user)
                .where(user.id.eq(userId))
                .fetchOne();

        return userExists != null;
    }

    @Override
    public Optional<ReviewUserDomain> findById(UUID userId) {
        User userEntity = em.find(User.class, userId);
        return Optional.ofNullable(userEntity)
                .map(ue -> new ReviewUserDomain(ue.getId(), ue.getNickname()));
    }
}
