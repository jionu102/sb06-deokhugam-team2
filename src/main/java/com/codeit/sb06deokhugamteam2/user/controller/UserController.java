package com.codeit.sb06deokhugamteam2.user.controller;


import com.codeit.sb06deokhugamteam2.common.enums.PeriodType;
import com.codeit.sb06deokhugamteam2.common.enums.RankingType;
import com.codeit.sb06deokhugamteam2.common.exception.ErrorCode;
import com.codeit.sb06deokhugamteam2.common.exception.exceptions.BasicException;
import com.codeit.sb06deokhugamteam2.user.dto.CursorPageResponse;
import com.codeit.sb06deokhugamteam2.user.dto.PowerUserDto;
import com.codeit.sb06deokhugamteam2.user.dto.UserDto;
import com.codeit.sb06deokhugamteam2.user.dto.UserLoginRequest;
import com.codeit.sb06deokhugamteam2.user.dto.UserRegisterRequest;
import com.codeit.sb06deokhugamteam2.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> register(@Valid @RequestBody UserRegisterRequest request) {
        UserDto userDto = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@Valid @RequestBody UserLoginRequest request) {
        UserDto userDto = userService.login(request);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserInfo(@PathVariable UUID userId) {
        UserDto userDto = userService.getUserInfo(userId);
        return ResponseEntity.ok(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> updateNickname(
            @PathVariable UUID userId,
            HttpServletRequest request) {
        String rawNicknameString;
        try {
            rawNicknameString = request.getReader().lines()
                    .reduce("", (accumulator, actual) -> accumulator + actual);
        } catch (java.io.IOException e) {
            throw new BasicException(ErrorCode.INVALID_USER_DATA,
                    Collections.singletonMap("error", "요청 본문, 읽기 실패"),
                    HttpStatus.BAD_REQUEST);
        }

        UserDto userDto = userService.updateNicknameFromRawString(userId, rawNicknameString);
        return ResponseEntity.ok(userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> softDeleteUser(@PathVariable UUID userId) {
        userService.softDeleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}/hard")
    public ResponseEntity<Void> hardDeleteUser(@PathVariable UUID userId) {
        userService.hardDeleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/power")
    public ResponseEntity<CursorPageResponse<PowerUserDto>> getPowerUsers(
            @RequestParam(defaultValue = "DAILY") PeriodType period,
            @RequestParam(defaultValue = "USER") RankingType rankingType,
            @RequestParam(defaultValue = "DESC") String direction,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime after,
            @RequestParam(defaultValue = "50") int limit) {

        LocalDateTime effectiveAfter = (after != null) ? after : LocalDateTime.now();

        CursorPageResponse<PowerUserDto> response = userService.getPowerUsers(
                period, rankingType, direction, cursor, effectiveAfter, limit);

        return ResponseEntity.ok(response);
    }
}
