package com.codeit.sb06deokhugamteam2.review.adapter.in;

import com.codeit.sb06deokhugamteam2.review.adapter.in.dto.CursorPageRequestReviewDto;
import com.codeit.sb06deokhugamteam2.review.adapter.in.dto.CursorPageResponseReviewDto;
import com.codeit.sb06deokhugamteam2.review.adapter.in.dto.ReviewCreateRequest;
import com.codeit.sb06deokhugamteam2.review.adapter.in.dto.ReviewDto;
import com.codeit.sb06deokhugamteam2.review.application.dto.ReviewDetail;
import com.codeit.sb06deokhugamteam2.review.application.port.in.query.ReviewQuery;
import com.codeit.sb06deokhugamteam2.review.application.dto.ReviewSummary;
import com.codeit.sb06deokhugamteam2.review.application.port.in.CreateReviewUseCase;
import com.codeit.sb06deokhugamteam2.review.application.port.in.GetReviewQuery;
import com.codeit.sb06deokhugamteam2.review.application.port.in.command.CreateReviewCommand;
import com.codeit.sb06deokhugamteam2.review.application.port.in.query.ReviewPaginationQuery;
import jakarta.validation.Valid;
import org.hibernate.validator.constraints.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/reviews")
public class ReviewController implements ReviewApi {

    private final ReviewApiMapper reviewMapper;
    private final CreateReviewUseCase createReviewUseCase;
    private final GetReviewQuery getReviewQuery;

    public ReviewController(
            ReviewApiMapper reviewMapper,
            CreateReviewUseCase createReviewUseCase,
            GetReviewQuery getReviewQuery
    ) {
        this.reviewMapper = reviewMapper;
        this.createReviewUseCase = createReviewUseCase;
        this.getReviewQuery = getReviewQuery;
    }

    @Override
    @PostMapping
    public ResponseEntity<ReviewDto> postReview(@RequestBody @Valid ReviewCreateRequest request) {
        CreateReviewCommand command = reviewMapper.toCreateReviewCommand(request);
        ReviewDetail detail = createReviewUseCase.createReview(command);
        ReviewDto response = reviewMapper.toReviewDetailResponse(detail);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @GetMapping
    public ResponseEntity<CursorPageResponseReviewDto> getReviews(
            @ModelAttribute
            @Valid
            CursorPageRequestReviewDto request,

            @RequestHeader(value = "Deokhugam-Request-User-ID")
            @UUID(message = "요청 사용자 ID는 UUID 형식이어야 합니다.")
            String header
    ) {
        ReviewPaginationQuery query = reviewMapper.toReviewPaginationQuery(request, header);
        ReviewSummary summary = getReviewQuery.readReviews(query);
        CursorPageResponseReviewDto response = reviewMapper.toReviewSummaryResponse(summary);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewDto> getReview(
            @PathVariable(name = "reviewId")
            @UUID(message = "리뷰 ID는 UUID 형식이어야 합니다.")
            String request,

            @RequestHeader(value = "Deokhugam-Request-User-ID")
            @UUID(message = "요청 사용자 ID는 UUID 형식이어야 합니다.")
            String header
    ) {
        ReviewQuery query = reviewMapper.toReviewQuery(request, header);
        ReviewDetail detail = getReviewQuery.readReview(query);
        ReviewDto response = reviewMapper.toReviewDetailResponse(detail);
        return ResponseEntity.ok(response);
    }
}
