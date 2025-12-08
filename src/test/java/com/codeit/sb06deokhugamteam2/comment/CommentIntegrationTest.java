package com.codeit.sb06deokhugamteam2.comment;

import com.codeit.sb06deokhugamteam2.comment.dto.CommentCreateRequest;
import com.codeit.sb06deokhugamteam2.comment.dto.CommentDto;
import com.codeit.sb06deokhugamteam2.comment.dto.CommentUpdateRequest;
import com.codeit.sb06deokhugamteam2.comment.dto.CursorPageResponseCommentDto;
import com.codeit.sb06deokhugamteam2.comment.entity.Comment;
import com.codeit.sb06deokhugamteam2.comment.repository.CommentRepository;
import com.codeit.sb06deokhugamteam2.review.adapter.out.entity.Review;
import com.codeit.sb06deokhugamteam2.review.adapter.out.entity.ReviewStat;
import com.codeit.sb06deokhugamteam2.user.entity.User;
import com.codeit.sb06deokhugamteam2.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class CommentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;


    @Test
    @DisplayName("댓글 생성 성공 API 테스트")
    void createComment_success() throws Exception {
        //given

        //사용자 생성 및 저장
        User user = User.builder()
                .email("test@mail.com")
                .nickname("testUser")
                .password("1234")
                .build();

        userRepository.saveAndFlush(user);

        //리뷰 생성 및 저장

        UUID reviewId = UUID.randomUUID();

        Review review = new Review()
                .id(reviewId)
                .book(null)
                .user(user)
                .rating(5)
                .content("review content")
                .createdAt(Instant.now())
                .updatedAt(Instant.now());

        ReviewStat stat = new ReviewStat()
                .id(reviewId)
                .review(review)
                .likeCount(0)
                .commentCount(0);

        review.reviewStat(stat);

        em.persist(review);
        em.persist(stat);
        em.flush();

        CommentCreateRequest request = new CommentCreateRequest(
                user.getId().toString(),
                reviewId.toString(),
                "댓글 등록 테스트"
        );

        String requestJson = mapper.writeValueAsString(request);

        //when
        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
        ).andReturn();

        //then
        assertThat(result.getResponse().getStatus()).isEqualTo(201);

        CommentDto dto =
                mapper.readValue(result.getResponse().getContentAsString(), CommentDto.class);

        assertThat(dto.userId()).isEqualTo(user.getId().toString());
        assertThat(dto.reviewId()).isEqualTo(reviewId.toString());
        assertThat(dto.userNickname()).isEqualTo("testUser");
        assertThat(dto.content()).isEqualTo("댓글 등록 테스트");

        //db 검증
        List<Comment> all = commentRepository.findAll();
        assertThat(all).hasSize(1);

        Comment saved = all.get(0);
        assertThat(saved.getContent()).isEqualTo("댓글 내용 테스트");
        assertThat(saved.getUser().getId()).isEqualTo(user.getId());
        assertThat(saved.getReview().id()).isEqualTo(reviewId);
    }

    @Test
    @DisplayName("댓글 수정 성공 API 테스트")
    void updateComment_success() throws Exception {
        //given
        //사용자 생성
        User user = User.builder()
                .email("test@mail.com")
                .nickname("testUser")
                .password("1234")
                .build();
        userRepository.saveAndFlush(user);

        //리뷰 생성
        UUID reviewId = UUID.randomUUID();

        Review review = new Review()
                .id(reviewId)
                .book(null)
                .user(user)
                .rating(5)
                .content("review content")
                .createdAt(Instant.now())
                .updatedAt(Instant.now());

        ReviewStat stat = new ReviewStat()
                .id(reviewId)
                .review(review)
                .likeCount(0)
                .commentCount(0);

        review.reviewStat(stat);

        em.persist(review);
        em.persist(stat);
        em.flush();

        //댓글 생성
        Comment comment = Comment.builder()
                .user(user)
                .review(review)
                .content("수정전 댓글 내용")
                .build();

        em.persist(comment);
        em.flush();

        UUID commentId = comment.getId();

        //수정 요청 DTO
        CommentUpdateRequest request = new CommentUpdateRequest("수정후 댓글 내용");
        String requestJson = mapper.writeValueAsString(request);

        //when
        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/comments/" + commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .header("Deokhugam-Request-User-Id", user.getId().toString())
        ).andReturn();

        //then
        assertThat(result.getResponse().getStatus()).isEqualTo(200);

        CommentDto dto =
                mapper.readValue(result.getResponse().getContentAsString(), CommentDto.class);

        assertThat(dto.id()).isEqualTo(commentId.toString());
        assertThat(dto.content()).isEqualTo("수정후 댓글 내용");

        Comment updated = commentRepository.findById(commentId).orElseThrow();
        assertThat(updated.getContent()).isEqualTo("수정후 댓글 내용");
    }


    @Test
    @DisplayName("댓글 단건 조회 성공 API 테스트")
    void readComment_success() throws Exception {

        //given
        //사용자 생성
        User user = User.builder()
                .email("test@mail.com")
                .nickname("testUser")
                .password("1234")
                .build();
        userRepository.saveAndFlush(user);

        //리뷰 생성
        UUID reviewId = UUID.randomUUID();

        Review review = new Review()
                .id(reviewId)
                .book(null)
                .user(user)
                .rating(5)
                .content("review content")
                .createdAt(Instant.now())
                .updatedAt(Instant.now());

        ReviewStat stat = new ReviewStat()
                .id(reviewId)
                .review(review)
                .likeCount(0)
                .commentCount(0);

        review.reviewStat(stat);

        em.persist(review);
        em.persist(stat);
        em.flush();

        //댓글 생성
        Comment comment = Comment.builder()
                .user(user)
                .review(review)
                .content("조회될 댓글 내용")
                .build();

        em.persist(comment);
        em.flush();

        UUID commentId = comment.getId();

        //when
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/comments/" + commentId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andReturn();

        //then
        assertThat(result.getResponse().getStatus()).isEqualTo(200);

        CommentDto dto =
                mapper.readValue(result.getResponse().getContentAsString(), CommentDto.class);

        assertThat(dto.id()).isEqualTo(commentId.toString());
        assertThat(dto.userId()).isEqualTo(user.getId().toString());
        assertThat(dto.reviewId()).isEqualTo(reviewId.toString());
        assertThat(dto.userNickname()).isEqualTo("testUser");
        assertThat(dto.content()).isEqualTo("조회될 댓글 내용");

        //db 검증
        Comment found = commentRepository.findById(commentId).orElseThrow();
        assertThat(found.getContent()).isEqualTo("조회될 댓글 내용");
    }

    @Test
    @DisplayName("댓글 목록 조회 성공 API 테스트")
    void readComments_success() throws Exception {
        //given
        //사용자 생성
        User user = User.builder()
                .email("test@mail.com")
                .nickname("testUser")
                .password("1234")
                .build();
        userRepository.saveAndFlush(user);

        //리뷰 생성
        UUID reviewId = UUID.randomUUID();

        Review review = new Review()
                .id(reviewId)
                .book(null)
                .user(user)
                .rating(5)
                .content("review content")
                .createdAt(Instant.now())
                .updatedAt(Instant.now());

        ReviewStat stat = new ReviewStat()
                .id(reviewId)
                .review(review)
                .likeCount(0)
                .commentCount(0);

        review.reviewStat(stat);

        em.persist(review);
        em.persist(stat);
        em.flush();

        //댓글 3개 등록
        Comment c1 = Comment.builder()
                .user(user)
                .review(review)
                .content("테스트 댓글1")
                .build();
        em.persist(c1);

        Thread.sleep(5); // createdAt 차이를 내기 위한 지연

        Comment c2 = Comment.builder()
                .user(user)
                .review(review)
                .content("테스트 댓글2")
                .build();
        em.persist(c2);

        Thread.sleep(5);

        Comment c3 = Comment.builder()
                .user(user)
                .review(review)
                .content("테스트 댓글3")
                .build();
        em.persist(c3);
        em.flush();

        //when
        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/comments")
                        .param("reviewId", reviewId.toString())
                        .param("direction", "DESC")
                        .param("size", "2")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn();

        //then
        assertThat(result.getResponse().getStatus()).isEqualTo(200);

        CursorPageResponseCommentDto dto =
                mapper.readValue(result.getResponse().getContentAsString(), CursorPageResponseCommentDto.class);

        // DTO 기본 필드 검증
        assertThat(dto.content().size()).isEqualTo(2);
        assertThat(dto.size()).isEqualTo(2);
        assertThat(dto.hasNext()).isTrue(); // 3개 중 2개만 가져왔으므로

        // DESC 정렬 → 가장 최신인 c3, 그 다음 c2
        assertThat(dto.content().get(0).content()).isEqualTo("테스트 댓글3");
        assertThat(dto.content().get(1).content()).isEqualTo("테스트 댓글2");

        // nextCursor / nextAfter 검증
        assertThat(dto.nextCursor()).isEqualTo(c2.getId().toString());
        assertThat(dto.nextAfter()).isEqualTo(c2.getCreatedAt());
    }

    @Test
    @DisplayName("댓글 논리 삭제 성공 API 테스트")
    void softDeleteComment_success() throws Exception {
        //given
        //사용자 생성
        User user = User.builder()
                .email("test@mail.com")
                .nickname("testUser")
                .password("1234")
                .build();
        userRepository.saveAndFlush(user);

        //리뷰 생성
        UUID reviewId = UUID.randomUUID();

        Review review = new Review()
                .id(reviewId)
                .book(null)
                .user(user)
                .rating(5)
                .content("review content")
                .createdAt(Instant.now())
                .updatedAt(Instant.now());

        ReviewStat stat = new ReviewStat()
                .id(reviewId)
                .review(review)
                .likeCount(0)
                .commentCount(0);

        review.reviewStat(stat);

        em.persist(review);
        em.persist(stat);
        em.flush();

        //댓글 생성
        Comment comment = Comment.builder()
                .user(user)
                .review(review)
                .content("논리삭제될 댓글")
                .build();

        em.persist(comment);
        em.flush();

        UUID commentId = comment.getId();

        //when
        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/comments/" + commentId)
                        .header("Deokhugam-Request-User-Id", user.getId().toString())
        ).andReturn();

        //then
        assertThat(result.getResponse().getStatus()).isEqualTo(204);

        // SoftDelete 적용 → findById는 삭제된 엔티티도 가져올 수 있음
        Comment deleted = commentRepository.findById(commentId).orElse(null);
        assertThat(deleted).isNotNull();

        // 하지만 findAll에서는 조회되지 않아야 함
        List<Comment> comments = commentRepository.findAll();
        assertThat(comments).isEmpty();
    }

    @Test
    @DisplayName("댓글 물리 삭제 성공 API 테스트")
    void hardDeleteComment_success() throws Exception {
        //given
        //사용자 생성
        User user = User.builder()
                .email("test@mail.com")
                .nickname("testUser")
                .password("1234")
                .build();
        userRepository.saveAndFlush(user);

        //리뷰 생성
        UUID reviewId = UUID.randomUUID();

        Review review = new Review()
                .id(reviewId)
                .book(null)
                .user(user)
                .rating(5)
                .content("review content")
                .createdAt(Instant.now())
                .updatedAt(Instant.now());

        ReviewStat stat = new ReviewStat()
                .id(reviewId)
                .review(review)
                .likeCount(0)
                .commentCount(0);

        review.reviewStat(stat);

        em.persist(review);
        em.persist(stat);
        em.flush();

        //댓글 생성
        Comment comment = Comment.builder()
                .user(user)
                .review(review)
                .content("물리삭제될 댓글")
                .build();

        em.persist(comment);
        em.flush();

        UUID commentId = comment.getId();

        //when
        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/comments/" + commentId + "/hard")
                        .header("Deokhugam-Request-User-Id", user.getId().toString())
        ).andReturn();


        //then
        assertThat(result.getResponse().getStatus()).isEqualTo(204);

        // 하드 삭제 → DB에서 실제로 사라져야 함
        Optional<Comment> found = commentRepository.findById(commentId);
        assertThat(found).isEmpty();

        // 전체 리스트에서도 없어야 함
        List<Comment> all = commentRepository.findAll();
        assertThat(all).isEmpty();
    }
}
