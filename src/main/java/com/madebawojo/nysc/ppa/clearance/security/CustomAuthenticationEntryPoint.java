package com.madebawojo.nysc.ppa.clearance.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.madebawojo.nysc.ppa.clearance.dto.response.ApiResponseStructure;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("message", "Unauthorized: Authentication is required to access this resource");
        body.put("data", null);
        body.put("errors", null);
        body.put("statusCode", 401);
        body.put("timestamp", Instant.now());

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
