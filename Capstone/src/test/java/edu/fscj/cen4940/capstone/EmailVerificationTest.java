package edu.fscj.cen4940.capstone;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.fscj.cen4940.capstone.auth.TempPasswordReset;
import edu.fscj.cen4940.capstone.auth.TempPasswordResetStorage;
import edu.fscj.cen4940.capstone.config.TestHelperConfig;
import edu.fscj.cen4940.capstone.dto.EmailVerificationDTO;
import edu.fscj.cen4940.capstone.entity.User;
import edu.fscj.cen4940.capstone.service.EmailService;
import edu.fscj.cen4940.capstone.service.UserService;
import edu.fscj.cen4940.capstone.util.CreateAndPersist;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(TestHelperConfig.class)
@WithMockUser(username="testuser@example.com", password="password123", roles={"USER"})
@DisplayName("Email Verification Integration Tests")
public class EmailVerificationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private CreateAndPersist create;

    @MockBean
    private EmailService emailService; // Use MockBean so Spring injects it

    private final String testUsername = "testuser@example.com";

    @Test
    @DisplayName("Full email verification flow")
    void testEmailVerificationFlow() throws Exception {
        // Create test user in H2
        User user = create.user("testuser");
        user.setUsername(testUsername);
        user.setEmailVerified(false);
        userService.save(user);

        // Mock email sending
        doNothing().when(emailService).sendVerificationEmail(anyString(), anyString());

        // Step 1: send verification email
        mockMvc.perform(post("/api/auth/send-verification-email")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Step 2: get code from DB
        User fromDb = userService.findByUsername(testUsername);
        String code = fromDb.getEmailVerificationCode();

        // Step 3: verify email
        EmailVerificationDTO dto = new EmailVerificationDTO();
        dto.setCode(code);

        mockMvc.perform(post("/api/auth/verify-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(request -> {
                            request.setRemoteUser(testUsername);
                            return request;
                        }))
                .andExpect(status().isOk());

        // Step 4: confirm emailVerified
        fromDb = userService.findByUsername(testUsername);
        assert fromDb.isEmailVerified();
    }

    @Test
    @DisplayName("Forgot password sends verification code")
    void forgotPassword_ShouldSendVerificationCode() throws Exception {
        User user = create.user("resetuser");
        user.setEmail("resetuser@example.com");
        userService.save(user);

        // Mock email sending
        doNothing().when(emailService).sendVerificationEmail(anyString(), anyString());

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"resetuser@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok").value(true))
                .andExpect(jsonPath("$.message").exists());

        // Ensure temp code exists in storage
        assertNotNull(TempPasswordResetStorage.getInstance().get("resetuser@example.com"));
    }

    @Test
    @DisplayName("Verify reset code returns ok for valid code")
    void verifyResetCode_Valid_ShouldReturnOk() throws Exception {
        String email = "resetuser@example.com";
        String code = "123456";
        TempPasswordResetStorage.getInstance().save(email, new TempPasswordReset(email, code));

        mockMvc.perform(post("/api/auth/verify-reset-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\", \"code\":\"" + code + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok").value(true))
                .andExpect(jsonPath("$.message").value("Code verified"));
    }

    @Test
    @DisplayName("Reset password updates user password with valid code")
    void resetPassword_Valid_ShouldUpdatePassword() throws Exception {
        User user = create.user("resetuser");
        user.setEmail("resetuser@example.com");
        userService.save(user);

        String code = "123456";
        TempPasswordResetStorage.getInstance().save(user.getEmail(), new TempPasswordReset(user.getEmail(), code));

        String newPassword = "newPassword123";
        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + user.getEmail() + "\", \"password\":\"" + newPassword + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok").value(true))
                .andExpect(jsonPath("$.message").value("Password reset successfully."));

        // Check that temp reset is removed
        assertNull(TempPasswordResetStorage.getInstance().get(user.getEmail()));

        // Optionally, verify new password hash/salt is set (not the raw password)
        User updatedUser = userService.findByUsername(user.getUsername());
        assertNotNull(updatedUser.getHash());
        assertNotNull(updatedUser.getSalt());
    }

}