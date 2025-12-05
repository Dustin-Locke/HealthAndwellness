package edu.fscj.cen4940.capstone.jwt.filters;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import edu.fscj.cen4940.capstone.auth.ApplicationUserDetailsService;
import edu.fscj.cen4940.capstone.jwt.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final ApplicationUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

//   @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) {
//        System.out.println("Incoming Request Path: " + request.getRequestURI());
//
//        String path = request.getRequestURI();
//
//        return path.equals("/api/reminders") ||
//            path.startsWith("/api/reminders/") ||
//            path.startsWith("/api/auth/") ||
//            path.startsWith("/user/") ||
//            path.startsWith("/food/") ||
//            path.startsWith("/meals/") ||
//            path.startsWith("/api/profile/") ||
//            path.startsWith("/api/weighin/") ||
//            path.startsWith("/swagger-ui/") ||
//            path.startsWith("/v3/api-docs/");
//    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        System.out.println("JwtRequestFilter: Incoming request to " + path);

        // ⭐ Skip JWT check for registration endpoints
        if (path.equals("/api/auth/pre-register") || path.equals("/api/auth/complete-registration")) {
            System.out.println("JwtRequestFilter: PermitAll endpoint, skipping token check.");
            chain.doFilter(request, response);
            return;
        }

        // Read the Authorization header
        final String authorizationHeader = request.getHeader("Authorization");
        System.out.println("JwtRequestFilter: Authorization header = " + authorizationHeader);

        String username = null;
        String token = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
            System.out.println("JwtRequestFilter: Extracted token = " + token);

            try {
                username = jwtUtil.extractUsername(token);
                System.out.println("JwtRequestFilter: Username extracted from token = " + username);
            } catch (Exception e) {
                System.out.println("JwtRequestFilter: Failed to extract username from token: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        } else {
            System.out.println("JwtRequestFilter: No token provided or invalid format.");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                if (jwtUtil.validateToken(token, userDetails)) {
                    System.out.println("JwtRequestFilter: Token valid, setting authentication.");
                    var authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    System.out.println("JwtRequestFilter: Token invalid.");
                }
            } catch (Exception e) {
                System.out.println("JwtRequestFilter: Failed to load user details or validate token: " + e.getMessage());
            }
        }

        System.out.println("JwtRequestFilter: Passing request to next filter.");
        chain.doFilter(request, response);
    }

}

