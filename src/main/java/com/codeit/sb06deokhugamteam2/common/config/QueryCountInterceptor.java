package com.codeit.sb06deokhugamteam2.common.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.ttddyy.dsproxy.QueryCount;
import net.ttddyy.dsproxy.QueryCountHolder;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class QueryCountInterceptor implements HandlerInterceptor {
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                @Nullable Exception ex) throws Exception {
        QueryCount queryCount = QueryCountHolder.get("datasource-proxy-logger");
        if (queryCount != null) {
            log.info("[datasource-proxy-logger] query count = {}", queryCount.getTotal());
        } else {
            log.warn("[datasource-proxy-logger] query count is null");
        }
        QueryCountHolder.clear();
    }
}