package com.samdev.videoOnDemand.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samdev.videoOnDemand.Entity.User;
import com.samdev.videoOnDemand.RequestDTO.UserDTO;
import com.samdev.videoOnDemand.Service.JWTService;
import com.samdev.videoOnDemand.Service.UserDetailService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@SuppressWarnings("unused")

public class SecurityFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final UserDetailService userDetailService;
    private static final Logger logger = LoggerFactory.getLogger(SecurityFilter.class);


    public SecurityFilter(JWTService jwtService, UserDetailService userDetailService) {
        this.jwtService = jwtService;
        this.userDetailService = userDetailService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException
    {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            logger.info("We are on the filter, {}",  authHeader);

            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);
            logger.info("We are on the username, {}",  username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userDetailService.loadUserByUsername(username);
                if (jwtService.isValid(token, user)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.info("JWT is valid, security context set.");
                }
            }

        } catch (ExpiredJwtException ex) {
            UserDTO userDAO = new UserDTO();
            userDAO.setStatusCode(HttpServletResponse.SC_UNAUTHORIZED);
            userDAO.setResponseMessage("JWT token expired");

            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(userDAO);

            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(json);
            return;
        }

        // 🔥 Always continue the chain if no early return
        filterChain.doFilter(request, response);
    }

}
