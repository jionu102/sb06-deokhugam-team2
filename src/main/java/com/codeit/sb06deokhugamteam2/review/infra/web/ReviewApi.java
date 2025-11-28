package com.codeit.sb06deokhugamteam2.review.infra.web;

import com.codeit.sb06deokhugamteam2.review.infra.web.dto.ReviewCreateRequest;
import com.codeit.sb06deokhugamteam2.review.infra.web.dto.ReviewDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "리뷰 관리", description = "리뷰 관련 API")
public interface ReviewApi {

    @Operation(summary = "리뷰 등록", description = "새로운 리뷰를 등록합니다.")
    @ApiResponse(responseCode = "201", description = "리뷰 등록 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 (입력값 검증 실패)")
    @ApiResponse(responseCode = "404", description = "도서 정보 없음")
    @ApiResponse(responseCode = "409", description = "이미 작성된 리뷰 존재")
    @ApiResponse(responseCode = "500", description = "서버 내부 요류")
    ResponseEntity<ReviewDto> postReview(@RequestBody(required = true) ReviewCreateRequest request);
}
