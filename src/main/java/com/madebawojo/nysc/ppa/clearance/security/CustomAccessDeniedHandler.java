package com.madebawojo.nysc.ppa.clearance.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.madebawojo.nysc.ppa.clearance.dto.response.ApiResponseStructure;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("success", false);
        responseBody.put("message", "Forbidden: You do not have permission to access this resource");
        responseBody.put("data", null);
        responseBody.put("errors", null);
        responseBody.put("statusCode", 403);
        responseBody.put("timestamp", Instant.now());

        objectMapper.writeValue(response.getOutputStream(), responseBody);
    }
}
