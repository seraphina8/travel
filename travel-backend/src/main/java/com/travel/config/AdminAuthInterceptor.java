package com.travel.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travel.common.Result;
import com.travel.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper;

    public AdminAuthInterceptor(JwtUtils jwtUtils, ObjectMapper objectMapper) {
        this.jwtUtils = jwtUtils;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (requiresAdmin(request) && !isAdminToken(request.getHeader("Authorization"))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(Result.error(401, "请先登录管理员账号")));
            return false;
        }
        return true;
    }

    private boolean isAdminToken(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            return false;
        }

        String token = authorization.startsWith("Bearer ") ? authorization.substring(7) : authorization;
        try {
            return !jwtUtils.isTokenExpired(token) && "admin".equals(jwtUtils.getTokenType(token));
        } catch (Exception e) {
            return false;
        }
    }

    private boolean requiresAdmin(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String method = request.getMethod();

        if ("OPTIONS".equalsIgnoreCase(method)) {
            return false;
        }

        if (uri.equals("/api/admin") || uri.startsWith("/api/admin/")) {
            return !uri.equals("/api/admin/login");
        }

        if (isUserManagement(uri, method)
                || isFeedbackManagement(uri)
                || isOrderManagement(uri, method)
                || isFoodManagement(uri, method)
                || isTagManagement(uri, method)) {
            return true;
        }

        if (isManagementResource(uri)) {
            return !"GET".equalsIgnoreCase(method);
        }

        return false;
    }

    private boolean isManagementResource(String uri) {
        return uri.startsWith("/api/scenic")
                || uri.startsWith("/api/attraction")
                || uri.startsWith("/api/ticket")
                || uri.startsWith("/api/banner");
    }

    private boolean isUserManagement(String uri, String method) {
        if (uri.equals("/api/user/list")) {
            return true;
        }
        if (uri.matches("^/api/user/\\d+/status$")) {
            return true;
        }
        return "DELETE".equalsIgnoreCase(method) && uri.matches("^/api/user/\\d+$");
    }

    private boolean isFeedbackManagement(String uri) {
        if (uri.equals("/api/feedback/submit") || uri.equals("/api/feedback/my")) {
            return false;
        }
        return uri.equals("/api/feedback")
                || uri.matches("^/api/feedback/\\d+$")
                || uri.matches("^/api/feedback/\\d+/reply$");
    }

    private boolean isOrderManagement(String uri, String method) {
        if ("GET".equalsIgnoreCase(method) && uri.equals("/api/order")) {
            return true;
        }
        if ("PUT".equalsIgnoreCase(method) && uri.matches("^/api/order/\\d+/status$")) {
            return true;
        }
        return "DELETE".equalsIgnoreCase(method) && uri.matches("^/api/order/\\d+$");
    }

    private boolean isFoodManagement(String uri, String method) {
        if ("POST".equalsIgnoreCase(method) && uri.equals("/api/food")) {
            return true;
        }
        if ("PUT".equalsIgnoreCase(method) && uri.matches("^/api/food/\\d+(/status)?$")) {
            return true;
        }
        return "DELETE".equalsIgnoreCase(method) && uri.matches("^/api/food/\\d+$");
    }

    private boolean isTagManagement(String uri, String method) {
        return uri.startsWith("/api/tag") && !"GET".equalsIgnoreCase(method);
    }
}
