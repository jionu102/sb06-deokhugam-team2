package com.codeit.sb06deokhugamteam2.comment.controller;

import com.codeit.sb06deokhugamteam2.comment.dto.CommentCreateRequest;
import com.codeit.sb06deokhugamteam2.comment.dto.CommentDto;
import com.codeit.sb06deokhugamteam2.comment.dto.CommentUpdateRequest;
import com.codeit.sb06deokhugamteam2.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentDto> createComment(
            @Valid @RequestBody CommentCreateRequest request) {
        log.info("createComment request={}", request);

        CommentDto response = commentService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable UUID commentId,
            @RequestHeader("Deokhugam-Request-User-Id") UUID userId,
            @Valid @RequestBody CommentUpdateRequest request
    ){
        log.info("updateComment request={}, commentId={}, userId={}", request, commentId, userId);

        CommentDto response = commentService.update(commentId,userId,request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
