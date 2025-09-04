package com.madebawojo.nysc.ppa.clearance.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.madebawojo.nysc.ppa.clearance.dto.response.ApiResponseStructure;
import com.madebawojo.nysc.ppa.clearance.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor // creates a constructor for any final variable in the class
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if(authHeader == null  || !authHeader.startsWith("Bearer ")){
//            filterChain.doFilter(request, response);
            writeApiResponse(response, "Authorization header missing or invalid", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        jwt = authHeader.substring(7);

        try{
            userEmail = jwtUtil.extractUsername(jwt); // username here is unique value attached to a user that will be used along with password for authentication
        } catch (JwtException | IllegalArgumentException e){
            writeApiResponse(response, "Invalid or expired token", 401);
            return;
        }

        if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails;
            try {
                userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            } catch (UsernameNotFoundException e) {
                writeApiResponse(response, "User not found", 401);
                return;
            }

            if(jwtUtil.isTokenValid(jwt, userDetails)){
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }

//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) {
//        String path = request.getRequestURI();
//        return path.startsWith("/h2-console") || path.startsWith("/api/v1/auth");
//    }
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/h2-console")
                || path.startsWith("/api/v1/auth")
                || path.startsWith("/swagger-ui")
                || path.equals("/swagger-ui.html")
                || path.startsWith("/v3/api-docs");
    }

    private void writeApiResponse(HttpServletResponse response, String message, int status) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("message", message);
        body.put("data", null);
        body.put("errors", null);
        body.put("statusCode", status);
        body.put("timestamp", Instant.now());

        objectMapper.writeValue(response.getOutputStream(), body);
    }

    private void writeErrorResponse(HttpServletResponse response, String message, int status) throws IOException {
        response.setContentType("application/json");
        response.setStatus(status);
        ApiResponseStructure<Object> errorResponse = ApiResponseStructure.error(message, status);
        new ObjectMapper().writeValue(response.getWriter(), errorResponse);
    }
}
