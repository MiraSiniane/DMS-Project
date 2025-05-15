package com.service.document.config;

import com.service.document.util.CustomUserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    //@Value("${app.jwtSecret}")
    private String jwtSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
                                    throws ServletException, IOException {
        String token = resolveToken(request);

        if (StringUtils.hasText(token)) {
            try {
                // Build signing key from secret
                Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

                // Parse and validate JWT (throws on expired/invalid)
                Claims claims = Jwts.parserBuilder()
                                    .setSigningKey(key)
                                    .build()
                                    .parseClaimsJws(token)
                                    .getBody();

                // 1) Extract user ID from subject
                Long userId = Long.parseLong(claims.getSubject());

                // 2) Extract roles list
                @SuppressWarnings("unchecked")
                List<String> roles = claims.get("roles", List.class);
                Collection<GrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

                // 3) Extract department IDs (as integers) → convert to Long
                @SuppressWarnings("unchecked")
                List<Integer> deptIdsInts = claims.get("deptIds", List.class);
                Collection<Long> deptIds = deptIdsInts.stream()
                    .map(Integer::longValue)
                    .collect(Collectors.toList());

                // 4) Build your custom principal
                CustomUserPrincipal principal =
                    new CustomUserPrincipal(userId, deptIds, authorities);

                // 5) Create authentication token
                Authentication auth =
                    new UsernamePasswordAuthenticationToken(principal, token, authorities);

                // 6) Store it
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (JwtException | IllegalArgumentException ex) {
                // Invalid or expired JWT – you can log here if you like
                SecurityContextHolder.clearContext();
            }
        }

        // Proceed down the filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * Grab the JWT from the Authorization header.
     */
    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
