package com.codeit.sb06deokhugamteam2.review.adapter.out;

import com.codeit.sb06deokhugamteam2.review.domain.port.ReviewUserRepository;
import com.codeit.sb06deokhugamteam2.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@Transactional(readOnly = true)
public class ReviewUserJpaRepository implements ReviewUserRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public boolean existsById(UUID userId) {
        User userEntity = em.find(User.class, userId);
        return userEntity != null;
    }
}
