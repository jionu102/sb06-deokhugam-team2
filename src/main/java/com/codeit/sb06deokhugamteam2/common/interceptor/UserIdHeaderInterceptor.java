package com.codeit.sb06deokhugamteam2.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


//로그인 성공시 요청헤더(Deokhugam-Request-User-ID)에 사용자ID 포함 기능
@Component
@Slf4j
public class UserIdHeaderInterceptor implements HandlerInterceptor {

    public static final String USER_ID_HEADER = "Deokhugam-Request-User-ID";

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler) throws Exception {

        // GET 요청은 모두 통과 (조회 기능은 인증 필수 아님)
        if (request.getMethod().equals("GET")) {
            return true;
        }

        // POST 요청 중 특정 경로 (회원가입, 로그인)는 통과
        if (request.getRequestURI().startsWith("/api/users") && (request.getMethod().equals("POST"))) {
            return true;
        }

        String userId = request.getHeader(USER_ID_HEADER);

        // 인증이 필요한 요청(PUT, DELETE 등)에서 헤더id가 없으면 에러 처리
        if (userId == null || userId.isEmpty()) {
            log.warn("Unauthorized Access: {} header missing for request URI: {}", USER_ID_HEADER, request.getRequestURI());

            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: User ID header missing.");
            return false;

        } else {
            log.info("Request authenticated. User ID: {}", userId);
        }

        return true;
    }
}