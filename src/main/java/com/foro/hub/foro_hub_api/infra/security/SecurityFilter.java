package com.foro.hub.foro_hub_api.infra.security;

import com.foro.hub.foro_hub_api.domain.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException  {
        System.out.println("Request URL: " + request.getRequestURI());
        var authHeader = request.getHeader("Authorization");
        System.out.println("Authorization Header: " + authHeader);

        if(authHeader != null) {
            String token = "";
            
            // Handle case-insensitive "bearer" and remove any quotes that might be present
            if (authHeader.toLowerCase().startsWith("bearer")) {
                // Fix for "bearer=" format (with equals sign)
                if (authHeader.toLowerCase().startsWith("bearer=")) {
                    token = authHeader.substring(7).trim();
                } else {
                    token = authHeader.substring(6).trim();
                }
                System.out.println("Bearer token extracted: " + token);
                
                // Remove surrounding quotes if present
                if (token.startsWith("\"") && token.endsWith("\"")) {
                    token = token.substring(1, token.length() - 1);
                    System.out.println("Token after quotes removal: " + token);
                }
            } else {
                // Assume the entire header is the token
                token = authHeader.trim();
                System.out.println("Using full header as token: " + token);
                
                // Remove surrounding quotes if present
                if (token.startsWith("\"") && token.endsWith("\"")) {
                    token = token.substring(1, token.length() - 1);
                    System.out.println("Token after quotes removal: " + token);
                }
            }
            
            // Check if token is empty
            if (token.trim().isEmpty()) {
                System.out.println("Token is empty, returning 401");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": \"Empty token provided\"}");
                return;
            }

            try {
                System.out.println("About to validate token: " + token);
                var username = tokenService.getSubject(token);
                System.out.println("Token validated successfully, username: " + username);
                
                if (username != null) {
                    var user = userRepository.findByLogin(username);
                    System.out.println("User found in repository: " + (user.isPresent() ? "Yes" : "No"));
                    
                    if (user.isEmpty()) {
                        System.out.println("User not found for token subject: " + username);
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("{\"error\": \"User not found for token\"}");
                        return;
                    }
                    
                    var authentication = new UsernamePasswordAuthenticationToken(
                            user.get(),
                            null,
                            user.get().getAuthorities()
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("Authentication set in SecurityContextHolder");
                }
            } catch (Exception e) {
                System.out.println("Exception during token validation: " + e.getMessage());
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
