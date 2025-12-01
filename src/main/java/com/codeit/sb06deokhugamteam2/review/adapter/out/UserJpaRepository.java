package com.codeit.sb06deokhugamteam2.review.adapter.out;

import com.codeit.sb06deokhugamteam2.review.application.port.out.UserRepository;
import com.codeit.sb06deokhugamteam2.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@Transactional(readOnly = true)
public class UserJpaRepository implements UserRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public boolean existsById(UUID userId) {
        User found = em.find(User.class, userId);
        return found != null;
    }
}
