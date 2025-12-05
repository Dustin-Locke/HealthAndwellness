package edu.fscj.cen4940.capstone.util;

import edu.fscj.cen4940.capstone.auth.AuthController.RegisterRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class Create {
    public RegisterRequest registerRequest(String email) {
        return new RegisterRequest(
                "Test",
                "User",
                email + "@example.com",  // <-- unique email
                LocalDate.of(2000,1,1),
                25,
                180.0,
                75.0,
                70.0,
                "password123"
        );
    }


}
