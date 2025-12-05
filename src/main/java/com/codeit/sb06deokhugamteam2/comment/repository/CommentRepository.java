package com.codeit.sb06deokhugamteam2.comment.repository;

import com.codeit.sb06deokhugamteam2.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID>, CommentQueryRepository {
}
