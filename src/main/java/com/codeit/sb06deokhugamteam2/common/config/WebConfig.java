package com.codeit.sb06deokhugamteam2.common.config;

import com.codeit.sb06deokhugamteam2.common.interceptor.UserIdHeaderInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final UserIdHeaderInterceptor userIdHeaderInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoggingInterceptor());
        registry.addInterceptor(new QueryCountInterceptor());

        registry.addInterceptor(userIdHeaderInterceptor)
                .addPathPatterns("/api/users/**");
    }
}
