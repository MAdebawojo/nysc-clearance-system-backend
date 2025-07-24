package com.madebawojo.nysc.ppa.clearance.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.madebawojo.nysc.ppa.clearance.dto.response.ApiResponseStructure;
import com.madebawojo.nysc.ppa.clearance.security.JwtService;
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

@Component
@RequiredArgsConstructor // creates a constructor for any final variable in the class
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if(authHeader == null  || !authHeader.startsWith("Bearer")){
            filterChain.doFilter(request, response);
            writeErrorResponse(response, "Authorization header missing or invalid", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        jwt = authHeader.substring(7);

        try{
            userEmail = jwtService.extractUsername(jwt); // username here is unique value attached to a user that will be used along with password for authentication
        } catch (JwtException | IllegalArgumentException e){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            writeErrorResponse(response, "Invalid or expired token", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails;
            try {
                userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            } catch (UsernameNotFoundException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                writeErrorResponse(response, "User not found", HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            if(jwtService.isTokenValid(jwt, userDetails)){
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

    private void writeErrorResponse(HttpServletResponse response, String message, int status) throws IOException {
        response.setContentType("application/json");
        response.setStatus(status);
        ApiResponseStructure<Object> errorResponse = ApiResponseStructure.error(message, status);
        new ObjectMapper().writeValue(response.getWriter(), errorResponse);
    }
}
