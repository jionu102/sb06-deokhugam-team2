package com.codeit.sb06deokhugamteam2.user.service;

import com.codeit.sb06deokhugamteam2.common.exception.ErrorCode;
import com.codeit.sb06deokhugamteam2.common.exception.exceptions.BasicException;
//import com.codeit.sb06deokhugamteam2.review.application.ReviewService;
//import com.codeit.sb06deokhugamteam2.user.dto.CursorPageResponse;
//import com.codeit.sb06deokhugamteam2.user.dto.PowerUserDto;
import com.codeit.sb06deokhugamteam2.user.dto.UserDto;
import com.codeit.sb06deokhugamteam2.user.dto.UserLoginRequest;
import com.codeit.sb06deokhugamteam2.user.dto.UserRegisterRequest;
import com.codeit.sb06deokhugamteam2.user.dto.UserUpdateRequest;
import com.codeit.sb06deokhugamteam2.user.entity.User;
import com.codeit.sb06deokhugamteam2.user.mapper.UserMapper;
//import com.codeit.sb06deokhugamteam2.user.repository.UserQueryRepository;
import com.codeit.sb06deokhugamteam2.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.util.Collections;
//import java.util.List;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    //    private final UserQueryRepository userQueryRepository;
    private final UserMapper userMapper;
//    private final ReviewService reviewService;
//    private final CommentService commentService;

    @Transactional
    public UserDto register(UserRegisterRequest request) {

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new BasicException(ErrorCode.DUPLICATE_EMAIL, Collections.emptyMap(),
                    HttpStatus.CONFLICT);
        }

        User user = userMapper.toEntity(request);

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    public UserDto login(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BasicException(ErrorCode.INVALID_USER_DATA,
                        Collections.emptyMap(), HttpStatus.BAD_REQUEST));

        if (user.getDeletedAt() != null) {
            throw new BasicException(ErrorCode.INVALID_USER_DATA,
                    Collections.emptyMap(), HttpStatus.BAD_REQUEST);
        }

        if (!request.password().equals(user.getPassword())) {
            throw new BasicException(ErrorCode.INVALID_USER_PASSWORD,
                    Collections.emptyMap(), HttpStatus.BAD_REQUEST);
        }

        return userMapper.toDto(user);
    }

    public UserDto getUserInfo(UUID userId) {
        //논리 삭제된 사용자, 조회에서 제외
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BasicException(ErrorCode.USER_NOT_FOUND,
                        Collections.emptyMap(), HttpStatus.NOT_FOUND));

        return userMapper.toDto(user);
    }

    @Transactional
    public void softDeleteUser(UUID userId) {

        if (!userRepository.existsById(userId)) {
            throw new BasicException(ErrorCode.USER_NOT_FOUND,
                    Collections.emptyMap(), HttpStatus.NOT_FOUND);
        }

        userRepository.deleteById(userId);
    }

    @Transactional
    public UserDto updateNickname(UUID userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BasicException(ErrorCode.USER_NOT_FOUND,
                        Collections.emptyMap(), HttpStatus.NOT_FOUND));

        user.updateNickname(request.nickname());
        return userMapper.toDto(user);
    }

//    public CursorPageResponse<PowerUserDto> getPowerUsers(
//            String period, String direction, String cursor, LocalDateTime after, int limit) {
//
//        PageRequest pageable = PageRequest.of(0, limit);
//        String afterString = (after != null) ? after.toString() : null;
//        return userQueryRepository.findPowerUsers(period, direction, cursor, afterString, pageable);
//    }

    @Transactional
    public void hardDeleteUser(UUID userId) {

        // reviewService.deleteAllByUserId(userId);
        // commentService.deleteAllByUserId(userId);

        userRepository.hardDeleteUserById(userId);
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

//      //연관 데이터 삭제 (필수)
//      reviewService.deleteAllByUserId(userId);
//      commentService.deleteAllByUserId(userId);

            //물리 삭제 실행
            userRepository.hardDeleteUserById(userId);
        }

        log.info("Batch Hard Delete: {} users successfully hard deleted.", userIds.size());
        return userIds.size();
    }
}