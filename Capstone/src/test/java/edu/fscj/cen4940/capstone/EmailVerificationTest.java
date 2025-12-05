package edu.fscj.cen4940.capstone;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.fscj.cen4940.capstone.config.TestHelperConfig;
import edu.fscj.cen4940.capstone.dto.EmailVerificationDTO;
import edu.fscj.cen4940.capstone.entity.User;
import edu.fscj.cen4940.capstone.service.EmailService;
import edu.fscj.cen4940.capstone.service.UserService;
import edu.fscj.cen4940.capstone.util.CreateAndPersist;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
}