package com.codeit.sb06deokhugamteam2.review.adapter.in;

import com.codeit.sb06deokhugamteam2.review.application.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.hibernate.validator.constraints.UUID;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;

@Tag(name = "리뷰 관리", description = "리뷰 관련 API")
public interface ReviewApi {

    @Operation(summary = "리뷰 등록", description = "새로운 리뷰를 등록합니다.")
    @ApiResponse(responseCode = "201", description = "리뷰 등록 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 (입력값 검증 실패)")
    @ApiResponse(responseCode = "404", description = "도서 정보 없음")
    @ApiResponse(responseCode = "409", description = "이미 작성된 리뷰 존재")
    @ApiResponse(responseCode = "500", description = "서버 내부 요류")
    ResponseEntity<ReviewDto> postReview(
            @RequestBody(required = true)
            @Valid
            ReviewCreateRequest requestBody
    );

    @Operation(summary = "리뷰 목록 조회", description = "검색 조건에 맞는 리뷰 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "리뷰 목록 조회 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 (정렬 기준 오류, 페이지네이션 파라미터 오류, 요청자 ID 누락)")
    @ApiResponse(responseCode = "500", description = "서버 내부 요류")
    ResponseEntity<CursorPageResponseReviewDto> getReviews(
            @ParameterObject
            @Valid
            CursorPageRequestReviewDto query,

            @Parameter(
                    required = true,
                    schema = @Schema(format = "uuid"),
                    in = ParameterIn.HEADER,
                    name = "Deokhugam-Request-User-ID",
                    description = "요청자 ID",
                    example = "123e4567-e89b-12d3-a456-426614174000"
            )
            @UUID(message = "요청 사용자 ID는 UUID 형식이어야 합니다.")
            String header
    );

    @Operation(summary = "리뷰 상세 정보 조회", description = "리뷰 ID로 상세 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "리뷰 정보 조회 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 (요청자 ID 누락)")
    @ApiResponse(responseCode = "404", description = "리뷰 정보 없음")
    @ApiResponse(responseCode = "500", description = "서버 내부 요류")
    ResponseEntity<ReviewDto> getReview(
            @Parameter(
                    required = true,
                    schema = @Schema(format = "uuid"),
                    in = ParameterIn.PATH,
                    description = "리뷰 ID",
                    example = "123e4567-e89b-12d3-a456-426614174000"
            )
            @UUID(message = "리뷰 ID는 UUID 형식이어야 합니다.")
            String path,

            @Parameter(
                    required = true,
                    schema = @Schema(format = "uuid"),
                    in = ParameterIn.HEADER,
                    name = "Deokhugam-Request-User-ID",
                    description = "요청자 ID",
                    example = "123e4567-e89b-12d3-a456-426614174000"
            )
            @UUID(message = "요청 사용자 ID는 UUID 형식이어야 합니다.")
            String header
    );

    @Operation(summary = "리뷰 논리 삭제", description = "본인이 작성한 리뷰를 논리적으로 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "리뷰 삭제 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 (요청자 ID 누락)")
    @ApiResponse(responseCode = "403", description = "리뷰 삭제 권한 없음")
    @ApiResponse(responseCode = "404", description = "리뷰 정보 없음")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    ResponseEntity<Void> deleteReview(
            @Parameter(
                    required = true,
                    schema = @Schema(format = "uuid"),
                    in = ParameterIn.PATH,
                    description = "리뷰 ID",
                    example = "123e4567-e89b-12d3-a456-426614174000"
            )
            @UUID(message = "리뷰 ID는 UUID 형식이어야 합니다.")
            String path,

            @Parameter(
                    required = true,
                    schema = @Schema(format = "uuid"),
                    in = ParameterIn.HEADER,
                    name = "Deokhugam-Request-User-ID",
                    description = "요청자 ID",
                    example = "123e4567-e89b-12d3-a456-426614174000"
            )
            @UUID(message = "요청 사용자 ID는 UUID 형식이어야 합니다.")
            String header
    );

    @Operation(summary = "리뷰 물리 삭제", description = "본인이 작성한 리뷰를 물리적으로 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "리뷰 삭제 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 (요청자 ID 누락)")
    @ApiResponse(responseCode = "403", description = "리뷰 삭제 권한 없음")
    @ApiResponse(responseCode = "404", description = "리뷰 정보 없음")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    ResponseEntity<Void> hardDeleteReview(
            @Parameter(
                    required = true,
                    schema = @Schema(format = "uuid"),
                    in = ParameterIn.PATH,
                    description = "리뷰 ID",
                    example = "123e4567-e89b-12d3-a456-426614174000"
            )
            @UUID(message = "리뷰 ID는 UUID 형식이어야 합니다.")
            String path,

            @Parameter(
                    required = true,
                    schema = @Schema(format = "uuid"),
                    in = ParameterIn.HEADER,
                    name = "Deokhugam-Request-User-ID",
                    description = "요청자 ID",
                    example = "123e4567-e89b-12d3-a456-426614174000"
            )
            @UUID(message = "요청 사용자 ID는 UUID 형식이어야 합니다.")
            String header
    );

    @Operation(summary = "리뷰 수정", description = "본인이 작성한 리뷰를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "리뷰 수정 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 (입력값 검증 실패)")
    @ApiResponse(responseCode = "403", description = "리뷰 수정 권한 없음")
    @ApiResponse(responseCode = "404", description = "리뷰 정보 없음")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    ResponseEntity<ReviewDto> patchReview(
            @Parameter(
                    required = true,
                    schema = @Schema(format = "uuid"),
                    in = ParameterIn.PATH,
                    description = "리뷰 ID",
                    example = "123e4567-e89b-12d3-a456-426614174000"
            )
            @UUID(message = "리뷰 ID는 UUID 형식이어야 합니다.")
            String path,

            @Parameter(
                    required = true,
                    schema = @Schema(format = "uuid"),
                    in = ParameterIn.HEADER,
                    name = "Deokhugam-Request-User-ID",
                    description = "요청자 ID",
                    example = "123e4567-e89b-12d3-a456-426614174000"
            )
            String header,

            @RequestBody(required = true)
            @Valid
            ReviewUpdateRequest requestBody
    );
}
