package edu.fscj.cen4940.capstone.auth;

import edu.fscj.cen4940.capstone.entity.User;
import edu.fscj.cen4940.capstone.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class AuthFailHandler implements AuthenticationFailureHandler {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException {
        String email = request.getParameter("email");

        User user = userRepository.findByEmail(email);
        if (user != null) {
            int failedAttempts = user.getFailedLoginAttempts();
            user.setFailedLoginAttempts(failedAttempts + 1);

            if (user.getFailedLoginAttempts() >= 3) {
                user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(15)); // Lock for 15 minutes
            }

            userRepository.save(user);
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Invalid credentials. Please try again.");
    }
}

