package com.codeit.sb06deokhugamteam2.user.service;

import com.codeit.sb06deokhugamteam2.common.enums.PeriodType;
import com.codeit.sb06deokhugamteam2.common.enums.RankingType;
import com.codeit.sb06deokhugamteam2.common.exception.ErrorCode;
import com.codeit.sb06deokhugamteam2.common.exception.exceptions.BasicException;
import com.codeit.sb06deokhugamteam2.common.exception.exceptions.UserException;
import com.codeit.sb06deokhugamteam2.user.dto.CursorPageResponse;
import com.codeit.sb06deokhugamteam2.user.dto.PowerUserDto;
import com.codeit.sb06deokhugamteam2.user.dto.UserDto;
import com.codeit.sb06deokhugamteam2.user.dto.UserLoginRequest;
import com.codeit.sb06deokhugamteam2.user.dto.UserRegisterRequest;
import com.codeit.sb06deokhugamteam2.user.dto.UserUpdateRequest;
import com.codeit.sb06deokhugamteam2.user.entity.User;
import com.codeit.sb06deokhugamteam2.user.mapper.UserMapper;
import com.codeit.sb06deokhugamteam2.user.repository.UserQueryRepository;
import com.codeit.sb06deokhugamteam2.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserQueryRepository userQueryRepository;
    private final UserMapper userMapper;
    private final Validator validator;
    // private final ReviewRepository reviewRepository;
    // private final CommentRepository commentRepository;

    @Transactional
    public UserDto register(UserRegisterRequest request) {

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new UserException(ErrorCode.DUPLICATE_EMAIL, Collections.emptyMap(),
                    HttpStatus.CONFLICT);
        }

        User user = userMapper.toEntity(request);

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    public UserDto login(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserException(ErrorCode.INVALID_USER_DATA, Collections.emptyMap(),
                    HttpStatus.BAD_REQUEST));

        if (user.getDeletedAt() != null) {
            throw new UserException(ErrorCode.INVALID_USER, Collections.emptyMap(),
                HttpStatus.BAD_REQUEST);
        }

        if (!request.password().equals(user.getPassword())) {
            throw new UserException(ErrorCode.INVALID_USER_DATA, Collections.emptyMap(),
                HttpStatus.BAD_REQUEST);
        }

        return userMapper.toDto(user);
    }

    public UserDto getUserInfo(UUID userId) {
        //논리 삭제된 사용자, 조회에서 제외
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND, Collections.emptyMap(),
                    HttpStatus.BAD_REQUEST));

        return userMapper.toDto(user);
    }

    @Transactional
    public void softDeleteUser(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND,
                        Collections.emptyMap(), HttpStatus.NOT_FOUND));

        userRepository.softDeleteAllReviewByUserId(userId);
        userRepository.softDeleteAllCommentByUserId(userId);
        userRepository.delete(user);
    }

    @Transactional
    public UserDto updateNicknameFromRawString(UUID userId, String rawNicknameString) {

        String nickname = rawNicknameString.trim().replaceAll("^\"|\"$", "");
        UserUpdateRequest validationRequest = new UserUpdateRequest(nickname);
        org.springframework.validation.Errors errors = new BeanPropertyBindingResult(validationRequest, "userUpdateRequest");
        validator.validate(validationRequest, errors);

        if (errors.hasErrors()) {

            String defaultMessage = errors.getFieldError().getDefaultMessage();
            throw new UserException(
                    ErrorCode.INVALID_USER_DATA,
                    Collections.singletonMap("message", defaultMessage),
                    HttpStatus.BAD_REQUEST
            );
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND, Collections.emptyMap(),
          HttpStatus.BAD_REQUEST));

        user.updateNickname(validationRequest.getNickname());

        return userMapper.toDto(user);
    }

    public CursorPageResponse<PowerUserDto> getPowerUsers(
            PeriodType period, RankingType rankingType, String direction, String cursor, LocalDateTime after, int limit) {

        PageRequest pageable = PageRequest.of(0, limit);
        String afterString = (after != null) ? after.toString() : null;
        return userQueryRepository.findPowerUsers(period, direction, cursor, afterString, pageable);
    }

    @Transactional
    public void hardDeleteUser(UUID userId) {

        // 1. 사용자 엔티티 조회 (리뷰/댓글 목록 Fetch Join을 통해 한 번에 로딩)
        User user = userQueryRepository.findByIdWithReviewsAndComments(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND, Collections.emptyMap(),
                    HttpStatus.BAD_REQUEST));

        userRepository.delete(user);
    }

    // 논리 삭제 후 1일이 경과한 사용자, 물리삭제
    @Transactional
    public int batchHardDeleteOldSoftDeletedUsers(double hoursAgo) {
        long secondsAgo = (long) (hoursAgo * 3600.0);
        LocalDateTime olderThan = LocalDateTime.now().minusSeconds(secondsAgo).withNano(0);

        //물리 삭제 대상자 ID 목록 조회
        List<UUID> userIds = userRepository.findHardDeleteCandidatesIgnoringRestriction(olderThan);

        if (userIds.isEmpty()) {
            log.info("Batch Hard Delete: No users found soft-deleted older than {} hours.", hoursAgo);
            return 0;
        }

        //일괄 물리 삭제 실행 (Hard Delete)
        for (UUID userId : userIds) {

            User user = userQueryRepository.findByIdWithReviewsAndComments(userId)
                    .orElse(null); // 배치에서는 404 예외 대신 null 처리 후 continue (이미 논리 삭제 대상이므로)

            if (user == null) {
                log.warn("Batch Hard Delete: User {} not found during review cleanup, skipping.", userId);
                continue;
            }

            userRepository.delete(user);
        }

        log.info("Batch Hard Delete: {} users successfully hard deleted.", userIds.size());
        return userIds.size();
    }
}
