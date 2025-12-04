package com.codeit.sb06deokhugamteam2.user.controller;


//import com.codeit.sb06deokhugamteam2.user.dto.CursorPageResponse;
//import com.codeit.sb06deokhugamteam2.user.dto.PowerUserDto;
import com.codeit.sb06deokhugamteam2.user.dto.UserDto;
import com.codeit.sb06deokhugamteam2.user.dto.UserLoginRequest;
import com.codeit.sb06deokhugamteam2.user.dto.UserRegisterRequest;
import com.codeit.sb06deokhugamteam2.user.dto.UserUpdateRequest;
import com.codeit.sb06deokhugamteam2.user.entity.User;
import com.codeit.sb06deokhugamteam2.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
//import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//import java.time.LocalDateTime;
//import java.util.Map;
import java.util.Map;
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
            @Valid @RequestBody UserUpdateRequest request) {
        UserDto userDto = userService.updateNickname(userId, request);
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

    @PostMapping("/batch-hard-delete")
    public ResponseEntity<Map<String, Integer>> batchHardDeleteOldSoftDeletedUsers(
            @RequestParam(defaultValue = "24") double hoursAgo) { // 기본값 1일(24시간)
        int deletedCount = userService.batchHardDeleteOldSoftDeletedUsers(hoursAgo);
        return ResponseEntity.ok(Map.of("deletedCount", deletedCount));
    }


//    @GetMapping("/power")
//    public ResponseEntity<CursorPageResponse<PowerUserDto>> getPowerUsers(
//            @RequestParam(defaultValue = "DAILY") String period,
//            @RequestParam(defaultValue = "DESC") String direction,
//            @RequestParam(required = false) String cursor,
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime after,
//            @RequestParam(defaultValue = "50") int limit) {
//
//        LocalDateTime effectiveAfter = (after != null) ? after : LocalDateTime.now();
//
//        CursorPageResponse<PowerUserDto> response = userService.getPowerUsers(
//                period, direction, cursor, effectiveAfter, limit);
//
//        return ResponseEntity.ok(response);
//    }
}