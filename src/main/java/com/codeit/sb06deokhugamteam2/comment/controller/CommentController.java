package com.codeit.sb06deokhugamteam2.comment.controller;

import com.codeit.sb06deokhugamteam2.comment.dto.CommentCreateRequest;
import com.codeit.sb06deokhugamteam2.comment.dto.CommentDto;
import com.codeit.sb06deokhugamteam2.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

        log.info("response={}", response);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
