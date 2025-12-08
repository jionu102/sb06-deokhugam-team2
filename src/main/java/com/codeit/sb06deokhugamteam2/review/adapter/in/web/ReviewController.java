package com.codeit.sb06deokhugamteam2.review.adapter.in.web;

import com.codeit.sb06deokhugamteam2.review.application.dto.request.CursorPageRequestReviewDto;
import com.codeit.sb06deokhugamteam2.review.application.dto.request.ReviewCreateRequest;
import com.codeit.sb06deokhugamteam2.review.application.dto.request.ReviewUpdateRequest;
import com.codeit.sb06deokhugamteam2.review.application.dto.response.CursorPageResponseReviewDto;
import com.codeit.sb06deokhugamteam2.review.application.dto.response.ReviewDto;
import com.codeit.sb06deokhugamteam2.review.application.dto.response.ReviewLikeDto;
import com.codeit.sb06deokhugamteam2.review.application.port.in.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/reviews")
public class ReviewController implements ReviewApi {

    private final CreateReviewUseCase createReviewUseCase;
    private final GetReviewUseCase getReviewUseCase;
    private final DeleteReviewUseCase deleteReviewUseCase;
    private final UpdateReviewUseCase updateReviewUseCase;
    private final ToggleReviewLikeUseCase toggleReviewLikeUseCase;

    public ReviewController(
            CreateReviewUseCase createReviewUseCase,
            GetReviewUseCase getReviewUseCase,
            DeleteReviewUseCase deleteReviewUseCase,
            UpdateReviewUseCase updateReviewUseCase,
            ToggleReviewLikeUseCase toggleReviewLikeUseCase
    ) {
        this.createReviewUseCase = createReviewUseCase;
        this.getReviewUseCase = getReviewUseCase;
        this.deleteReviewUseCase = deleteReviewUseCase;
        this.updateReviewUseCase = updateReviewUseCase;
        this.toggleReviewLikeUseCase = toggleReviewLikeUseCase;
    }

    @Override
    @PostMapping
    public ResponseEntity<ReviewDto> postReview(@RequestBody ReviewCreateRequest requestBody) {
        ReviewDto response = createReviewUseCase.createReview(requestBody);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @GetMapping
    public ResponseEntity<CursorPageResponseReviewDto> getReviews(
            @ModelAttribute CursorPageRequestReviewDto query,
            @RequestHeader(value = "Deokhugam-Request-User-ID") String header
    ) {
        CursorPageResponseReviewDto response = getReviewUseCase.readReviews(query, header);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewDto> getReview(
            @PathVariable(name = "reviewId") String path,
            @RequestHeader(value = "Deokhugam-Request-User-ID") String header
    ) {
        ReviewDto response = getReviewUseCase.readReview(path, header);
        return ResponseEntity.ok(response);
    }

    @Override
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable(name = "reviewId") String path,
            @RequestHeader(value = "Deokhugam-Request-User-ID") String header
    ) {
        deleteReviewUseCase.softDeleteReview(path, header);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @Override
    @DeleteMapping("/{reviewId}/hard")
    public ResponseEntity<Void> hardDeleteReview(
            @PathVariable(name = "reviewId") String path,
            @RequestHeader(value = "Deokhugam-Request-User-ID") String header
    ) {
        deleteReviewUseCase.deleteReview(path, header);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @Override
    @PatchMapping("/{reviewId}")
    public ResponseEntity<ReviewDto> patchReview(
            @PathVariable(name = "reviewId") String path,
            @RequestHeader(name = "Deokhugam-Request-User-ID") String header,
            @RequestBody ReviewUpdateRequest requestBody
    ) {
        ReviewDto response = updateReviewUseCase.updateReview(path, header, requestBody);
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/{reviewId}/like")
    public ResponseEntity<ReviewLikeDto> postReviewLike(
            @PathVariable(name = "reviewId") String path,
            @RequestHeader(name = "Deokhugam-Request-User-ID") String header
    ) {
        ReviewLikeDto response = toggleReviewLikeUseCase.toggleReviewLike(path, header);
        return ResponseEntity.ok(response);
    }
}
