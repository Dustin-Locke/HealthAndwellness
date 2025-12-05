package edu.fscj.cen4940.capstone;

import edu.fscj.cen4940.capstone.service.EmailService;
import edu.fscj.cen4940.capstone.service.UserService;
import edu.fscj.cen4940.capstone.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("Capstone Application Startup and Bean Tests")
class CapstoneApplicationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private EmailService emailService;

    @Test
    @DisplayName("Application Context Loads Successfully")
    void contextLoads() { }

    @Test
    @DisplayName("Verify UserService and UserRepository Beans Are Loaded")
    void verifyBeansAreLoaded() {
        assertThat(userService).as("UserService should be loaded").isNotNull();
        assertThat(userRepository).as("UserRepository should be loaded").isNotNull();
    }

    @Test
    @DisplayName("Verify UserService Password Hashing Functionality")
    void verifyUserServiceFunctionality() {
        String rawPassword = "password123";
        byte[] salt = userService.generateSalt();
        byte[] hashed = userService.hashPassword(rawPassword, salt);

        assertThat(hashed)
                .as("Hashed password should not be null or equal to the raw password")
                .isNotNull()
                .isNotEqualTo(rawPassword);
    }

}
