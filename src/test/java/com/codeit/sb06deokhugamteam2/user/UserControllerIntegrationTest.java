package com.codeit.sb06deokhugamteam2.user;

import com.codeit.sb06deokhugamteam2.book.repository.BookRepository;
import com.codeit.sb06deokhugamteam2.common.exception.ErrorCode;
import com.codeit.sb06deokhugamteam2.review.application.dto.request.ReviewCreateRequest;
import com.codeit.sb06deokhugamteam2.review.application.dto.response.ReviewDto;
import com.codeit.sb06deokhugamteam2.review.application.port.out.LoadReviewPort;
import com.codeit.sb06deokhugamteam2.review.application.service.ReviewCommandService;
import com.codeit.sb06deokhugamteam2.user.entity.User;
import com.codeit.sb06deokhugamteam2.user.dto.UserLoginRequest;
import com.codeit.sb06deokhugamteam2.user.dto.UserRegisterRequest;
import com.codeit.sb06deokhugamteam2.user.dto.UserUpdateRequest;
import com.codeit.sb06deokhugamteam2.book.entity.Book;
import com.codeit.sb06deokhugamteam2.user.repository.UserRepository;
import com.codeit.sb06deokhugamteam2.user.repository.UserQueryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReviewCommandService reviewCommandService;

    @Autowired
    private LoadReviewPort loadReviewPort;

    @Autowired
    private UserQueryRepository userQueryRepository;

    private User testUser;
    private final String TEST_EMAIL = "test@deokhugam.com";
    private final String TEST_PASSWORD = "Password123!"; // 평문 비밀번호
    private final String BASE_URL = "/api/users";


    @BeforeEach
    void setup() {

        if (userQueryRepository.findByEmailWithDeleted(TEST_EMAIL).isEmpty()) {
            testUser = User.builder()
                    .email(TEST_EMAIL)
                    .nickname("테스터")
                    .password(TEST_PASSWORD)
                    .createdAt(LocalDateTime.now())
                    .build();
            userRepository.save(testUser);
        } else {
            testUser = userQueryRepository.findByEmailWithDeleted(TEST_EMAIL).get();
        }
    }

    // 1. 회원가입 (POST /api/users) 테스트

    @Test
    @DisplayName("1-1. 회원가입 성공")
    void register_success() throws Exception {
        // given
        UserRegisterRequest request = new UserRegisterRequest(
                "newuser@test.com", "새로운유저", "NewPass456!"
        );

        // when & then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated()) // 201 Created
                .andExpect(jsonPath("$.email").value("newuser@test.com"))
                .andExpect(jsonPath("$.nickname").value("새로운유저"));
    }

    @Test
    @DisplayName("1-2. 회원가입 실패 - 이메일 중복")
    void register_fail_duplicate_email() throws Exception {
        // given
        UserRegisterRequest request = new UserRegisterRequest(
                TEST_EMAIL, "중복유저", "Duplicate789!"
        );

        // when & then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict()) // 409 Conflict

                .andExpect(jsonPath("$.code").value(ErrorCode.DUPLICATE_EMAIL.toString()));
    }

    @Test
    @DisplayName("1-3. 회원가입 실패 - 유효성 검증 실패")
    void register_fail_validation() throws Exception {
        // given: 닉네임 1자, 비밀번호 형식 오류 (MethodArgumentNotValidException 발생 기대)
        UserRegisterRequest request = new UserRegisterRequest(
                "invalid@email.com", "짧", "short"
        );

        // when & then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()) // 400 Bad Request
                .andExpect(jsonPath("$.code").value(ErrorCode.COMMON_EXCEPTION.toString()));
    }

    // 2. 로그인 (POST /api/users/login) 테스트

    @Test
    @DisplayName("2-1. 로그인 성공")
    void login_success() throws Exception {
        // given
        UserLoginRequest request = new UserLoginRequest(TEST_EMAIL, TEST_PASSWORD);

        // when & then
        mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()) // 200 OK
                .andExpect(jsonPath("$.email").value(TEST_EMAIL));
    }

    @Test
    @DisplayName("2-2. 로그인 실패 - 비밀번호 불일치")
    void login_fail_password() throws Exception {
        // given
        UserLoginRequest request = new UserLoginRequest(TEST_EMAIL, "WrongPassword");

        // when & then
        mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()) // 400 BAD_REQUEST

                .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_USER_PASSWORD.toString()));
    }

    // 3. 사용자 정보 조회 (GET /api/users/{userId}) 테스트

    @Test
    @DisplayName("3-1. 사용자 정보 조회 성공")
    void getUserInfo_success() throws Exception {
        // when & then
        mockMvc.perform(get(BASE_URL + "/{userId}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // 200 OK
                .andExpect(jsonPath("$.id").value(testUser.getId().toString()))
                .andExpect(jsonPath("$.nickname").value(testUser.getNickname()));
    }

    @Test
    @DisplayName("3-2. 사용자 정보 조회 실패 - 사용자 없음")
    void getUserInfo_fail_not_found() throws Exception {
        // given
        UUID nonExistentId = UUID.randomUUID();

        // when & then
        mockMvc.perform(get(BASE_URL + "/{userId}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()) // 404 Not Found
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.toString()));
    }

    // 4. 사용자 정보 수정 (PATCH /api/users/{userId}) 테스트

    @Test
    @DisplayName("4-1. 닉네임 수정 성공")
    void updateNickname_success() throws Exception {
        // given
        UserUpdateRequest request = new UserUpdateRequest("변경된닉네임");

        // when & then
        mockMvc.perform(patch(BASE_URL + "/{userId}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()) // 200 OK
                .andExpect(jsonPath("$.nickname").value("변경된닉네임"));

        // 데이터베이스에서 실제 변경 확인
        User updatedUser = userRepository.findById(testUser.getId()).get();
        assertThat(updatedUser.getNickname()).isEqualTo("변경된닉네임");
    }

    // 5. 사용자 논리 삭제 (DELETE /api/users/{userId}) 테스트

    @Test
    @DisplayName("5-1. 사용자 논리 삭제 성공")
    void softDeleteUser_success() throws Exception {
        // when
        mockMvc.perform(delete(BASE_URL + "/{userId}", testUser.getId()))
                .andExpect(status().isNoContent()); // 204 No Content

        // then 1: 일반 조회(findById)는 실패
        assertThat(userRepository.findById(testUser.getId())).isEmpty();

        // then 2: QueryDSL을 사용하여 deleted=true이고 deletedAt이 설정되었는지 확인
        User deletedUser = userQueryRepository.findByEmailWithDeleted(TEST_EMAIL)
                .orElseThrow(() -> new AssertionError("Soft Deleted User Not Found"));
        assertThat(deletedUser.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("6-1. 사용자 물리 삭제 성공 및 연관 리뷰 삭제 확인")
    void hardDeleteUser_success_with_review_deletion() throws Exception {

        Book testBook = Book.builder()
                .title("테스트 북")
                .author("테스터 저자")
                .description("테스트 도서 설명")
                .publisher("테스트 출판사")
                .publishedDate(java.time.LocalDate.now())
                .isbn("9781234567890")
                .build();
        bookRepository.save(testBook);

        ReviewCreateRequest reviewRequest = new ReviewCreateRequest(
                testBook.getId().toString(),
                testUser.getId().toString(),
                "최곱니다.",
                5
        );

        ReviewDto createdReview = reviewCommandService.createReview(reviewRequest);
        UUID createdReviewId = createdReview.id();

        // 리뷰가 DB에 존재하는지 확인
        assertThat(loadReviewPort.findById(createdReviewId, testUser.getId()).isPresent()).isTrue();


        // 2. when: 사용자 물리 삭제 API 호출
        mockMvc.perform(delete(BASE_URL + "/{userId}/hard", testUser.getId()))
                .andExpect(status().isNoContent()); // 204 No Content

        // 3. then:  유저가 DB에서 완전히 사라졌는지 확인
        assertThat(userQueryRepository.findByEmailWithDeleted(TEST_EMAIL)).isEmpty();
        assertThat(loadReviewPort.findById(createdReviewId, testUser.getId()).isEmpty()).isTrue();
    }
}
