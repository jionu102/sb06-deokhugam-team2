package com.codeit.sb06deokhugamteam2.common.config;

import com.codeit.sb06deokhugamteam2.common.exception.ErrorCode;
import com.codeit.sb06deokhugamteam2.common.exception.exceptions.MDCException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class LoggingInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        UUID uuid = UUID.randomUUID();
        try {
            MDC.put("ipAddress", Optional.ofNullable(request.getRemoteAddr()).orElse("-"));
            MDC.put("requestId", uuid.toString());
            MDC.put("requestUrl", Optional.ofNullable(request.getRequestURL()).map(StringBuffer::toString).orElse("-"));
            MDC.put("requestMethod", request.getMethod());
        } catch (Exception e) {
            throw new MDCException(ErrorCode.COMMON_EXCEPTION, Map.of("message", e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.addHeader("Deokhugam-request-id", uuid.toString());
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
        MDC.clear();
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

}
