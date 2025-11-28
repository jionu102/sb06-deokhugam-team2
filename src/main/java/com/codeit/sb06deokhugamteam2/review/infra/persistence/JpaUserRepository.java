package com.codeit.sb06deokhugamteam2.review.infra.persistence;

import com.codeit.sb06deokhugamteam2.review.domain.ReviewException;
import com.codeit.sb06deokhugamteam2.review.domain.UserRepository;
import com.codeit.sb06deokhugamteam2.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.function.Function;

@Component
@Transactional(readOnly = true)
public class JpaUserRepository implements UserRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void exists(UUID userId, Function<UUID, ? extends ReviewException> exceptionFunction) {
        User user = em.find(User.class, userId);
        if (user == null) {
            throw exceptionFunction.apply(userId);
        }
    }
}
