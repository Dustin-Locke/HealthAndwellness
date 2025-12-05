package edu.fscj.cen4940.capstone.auth;

import edu.fscj.cen4940.capstone.entity.User;
import edu.fscj.cen4940.capstone.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;


@Component
public class AuthSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email);
        if (user != null) {
            user.setFailedLoginAttempts(0); // Reset failed attempts on successful login
            userRepository.save(user);
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("Login successful.");
    }
}

