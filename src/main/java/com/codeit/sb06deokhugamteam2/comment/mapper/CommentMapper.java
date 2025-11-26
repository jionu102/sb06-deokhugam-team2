package com.codeit.sb06deokhugamteam2.comment.mapper;

import com.codeit.sb06deokhugamteam2.comment.entity.Comment;
import com.codeit.sb06deokhugamteam2.comment.dto.CommentDto;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    public CommentDto toDto(Comment comment) {
        return new CommentDto(
                comment.getId().toString(),
                comment.getUser().getId().toString(),
                comment.getReview().getId().toString(),
                comment.getUser().getNickname(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}
