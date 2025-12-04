package com.codeit.sb06deokhugamteam2.user.repository;

import com.codeit.sb06deokhugamteam2.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface UserRepository extends JpaRepository<User, UUID> {
    //UserService에서 findAll()후 필터링으로 성능문제발생, db에서 효율적인 조회를위해 필드추가
    Optional<User> findByEmail(String email);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "delete FROM USERS WHERE id = :userId", nativeQuery = true)
    void hardDeleteUserById(@Param("userId") UUID userId);

    // 물리 삭제 대상자 조회 못하는 문제로 JPQL 쿼리 추가 (엔티티에 @SQLRestriction 우회)
    @Query(value = """
    SELECT u.id
    FROM User u
    WHERE u.deletedAt IS NOT NULL
    AND u.deletedAt <= :olderThan
""")
    List<UUID> findHardDeleteCandidatesIgnoringRestriction(@Param("olderThan") LocalDateTime olderThan);
}