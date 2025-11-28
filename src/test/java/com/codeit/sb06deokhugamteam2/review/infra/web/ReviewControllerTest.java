package com.codeit.sb06deokhugamteam2.review.infra.web;

import com.codeit.sb06deokhugamteam2.review.infra.persistence.entity.Review;
import com.codeit.sb06deokhugamteam2.review.infra.web.dto.ReviewCreateRequest;
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
import java.util.UUID;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
@Transactional
class ReviewControllerTest {

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
        var request = new ReviewCreateRequest(UUID.fromString(bookId), UUID.fromString(userId), content, rating);
        Instant now = Instant.now();

        MvcTestResult result = mockMvc.post()
                .uri("/api/reviews")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
                .exchange();

        result.assertThat()
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .extractingPath("$.id")
                .asString()
                .satisfies(id -> {
                    Review saved = entityManager.find(Review.class, UUID.fromString(id));
                    assertSoftly(softly -> {
                        softly.assertThat(saved.id()).isEqualTo(UUID.fromString(id));
                        softly.assertThat(saved.rating()).isEqualTo(rating);
                        softly.assertThat(saved.content()).isEqualTo(content);
                        softly.assertThat(saved.likeCount()).isEqualTo(0);
                        softly.assertThat(saved.commentCount()).isEqualTo(0);
                        softly.assertThat(saved.createdAt()).isAfterOrEqualTo(now);
                        softly.assertThat(saved.updatedAt()).isEqualTo(saved.createdAt()).isAfterOrEqualTo(now);
                        softly.assertThat(saved.deleted()).isFalse();
                    });
                });
    }
}
