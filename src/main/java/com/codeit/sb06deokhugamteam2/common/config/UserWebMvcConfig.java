package com.codeit.sb06deokhugamteam2.common.config;

import com.codeit.sb06deokhugamteam2.common.interceptor.UserIdHeaderInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


//UserIdHeaderInterceptor 스프링 설정에 등록.
@Configuration
@RequiredArgsConstructor
public class UserWebMvcConfig implements WebMvcConfigurer {

    private final UserIdHeaderInterceptor userIdHeaderInterceptor;
    private final LoggingInterceptor loggingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // LoggingInterceptor 등록 (먼저 실행)
        registry.addInterceptor(loggingInterceptor)
                .addPathPatterns("/**") // 모든 경로에 적용
                .order(1); // 로깅순서

        // 모든 /api/users 경로의 요청에 인터셉터를 적용
        registry.addInterceptor(userIdHeaderInterceptor)
                .addPathPatterns("/api/users/**")
                .order(2);
    }
}