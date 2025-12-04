package com.codeit.sb06deokhugamteam2.review.adapter.in;

import com.codeit.sb06deokhugamteam2.review.application.dto.*;
import com.codeit.sb06deokhugamteam2.review.application.port.in.CreateReviewUseCase;
import com.codeit.sb06deokhugamteam2.review.application.port.in.DeleteReviewUseCase;
import com.codeit.sb06deokhugamteam2.review.application.port.in.GetReviewQuery;
import com.codeit.sb06deokhugamteam2.review.application.port.in.UpdateReviewUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/reviews")
public class ReviewController implements ReviewApi {

    private final CreateReviewUseCase createReviewUseCase;
    private final GetReviewQuery getReviewQuery;
    private final DeleteReviewUseCase deleteReviewUseCase;
    private final UpdateReviewUseCase updateReviewUseCase;

    public ReviewController(
            CreateReviewUseCase createReviewUseCase,
            GetReviewQuery getReviewQuery,
            DeleteReviewUseCase deleteReviewUseCase,
            UpdateReviewUseCase updateReviewUseCase
    ) {
        this.createReviewUseCase = createReviewUseCase;
        this.getReviewQuery = getReviewQuery;
        this.deleteReviewUseCase = deleteReviewUseCase;
        this.updateReviewUseCase = updateReviewUseCase;
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
        CursorPageResponseReviewDto response = getReviewQuery.readReviews(query, header);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewDto> getReview(
            @PathVariable(name = "reviewId") String path,
            @RequestHeader(value = "Deokhugam-Request-User-ID") String header
    ) {
        ReviewDto response = getReviewQuery.readReview(path, header);
        return ResponseEntity.ok(response);
    }

    @Override
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable(name = "reviewId") String path,
            @RequestHeader(value = "Deokhugam-Request-User-ID") String header
    ) {
        deleteReviewUseCase.deleteReview(path, header);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @Override
    @DeleteMapping("/{reviewId}/hard")
    public ResponseEntity<Void> hardDeleteReview(
            @PathVariable(name = "reviewId") String path,
            @RequestHeader(value = "Deokhugam-Request-User-ID") String header
    ) {
        deleteReviewUseCase.hardDeleteReview(path, header);
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
}
