package com.codeit.sb06deokhugamteam2.review;

import com.codeit.sb06deokhugamteam2.review.adapter.out.entity.Review;
import com.codeit.sb06deokhugamteam2.review.application.dto.request.ReviewCreateRequest;
import com.codeit.sb06deokhugamteam2.review.application.dto.response.ReviewDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
@Transactional
class ReviewTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvcTester mockMvc;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void whenPostReview_thenSuccess() throws Exception {
        final var bookId = "3fa85f64-5717-4562-b3fc-2c963f66afa6";
        final var userId = "3fa85f64-5717-4562-b3fc-2c963f66afa6";
        final var content = "리뷰입니다.";
        final var rating = 5;
        var request = new ReviewCreateRequest(bookId, userId, content, rating);
        Instant now = Instant.now();

        MvcTestResult result = mockMvc.post()
                .uri("/api/reviews")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
                .exchange();

        result.assertThat()
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .convertTo(ReviewDto.class)
                .satisfies(reviewDto -> {

                    // verify response
                    assertSoftly(softly -> {
                        softly.assertThat(reviewDto.id()).isNotNull();
                        softly.assertThat(reviewDto.bookId()).isEqualTo(bookId);
                        softly.assertThat(reviewDto.userId()).isEqualTo(userId);
                        softly.assertThat(reviewDto.rating()).isEqualTo(rating);
                        softly.assertThat(reviewDto.content()).isEqualTo(content);
                        softly.assertThat(reviewDto.likeCount()).isEqualTo(0);
                        softly.assertThat(reviewDto.commentCount()).isEqualTo(0);
                        softly.assertThat(reviewDto.likedByMe()).isFalse();
                    });

                    // verify entity
                    Review reviewEntity = entityManager.find(Review.class, reviewDto.id());
                    assertSoftly(softly -> {
                        softly.assertThat(reviewEntity.id()).isEqualTo(reviewDto.id());
                        softly.assertThat(reviewEntity.rating()).isEqualTo(reviewDto.rating());
                        softly.assertThat(reviewEntity.content()).isEqualTo(reviewDto.content());
                        softly.assertThat(reviewEntity.reviewStat().likeCount()).isEqualTo(0);
                        softly.assertThat(reviewEntity.reviewStat().commentCount()).isEqualTo(0);
                        softly.assertThat(reviewEntity.createdAt()).isAfterOrEqualTo(now);
                        softly.assertThat(reviewEntity.updatedAt())
                                .isEqualTo(reviewEntity.createdAt())
                                .isAfterOrEqualTo(now);
                    });
                });
    }
}
