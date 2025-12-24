package com.demo.TaskManager.security;

import com.demo.TaskManager.dtos.ErrorResponse;
import com.demo.TaskManager.entities.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtTokenService;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                if (jwtTokenService.validateToken(token)) {
                    String email = jwtTokenService.getUserFromToken(token);
                    Long userId = jwtTokenService.getUserIdFromToken(token);
                    User userEntity = new User();
                    userEntity.setId(userId);
                    userEntity.setEmail(email);

                    SecurityUser userDetails = new SecurityUser(userEntity);

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    writeErrorResponse(response,
                            HttpStatus.UNAUTHORIZED,
                            "Invalid or expired JWT token");
                    return;
                }
            } catch (Exception ex) {
                writeErrorResponse(response,
                        HttpStatus.FORBIDDEN,
                        "Malformed or invalid JWT: " + ex.getMessage());
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/auth/");
    }
    private void writeErrorResponse(HttpServletResponse response,
                                    HttpStatus status,
                                    String message) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(status, message);

        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(errorResponse));
    }
}