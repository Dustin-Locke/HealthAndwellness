package edu.fscj.cen4940.capstone;

import edu.fscj.cen4940.capstone.auth.AuthController.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.fscj.cen4940.capstone.dto.UserDTO;
import edu.fscj.cen4940.capstone.entity.User;
import edu.fscj.cen4940.capstone.repository.UserRepository;
import edu.fscj.cen4940.capstone.service.EmailService;
import edu.fscj.cen4940.capstone.service.UserService;

import jakarta.transaction.Transactional;
import edu.fscj.cen4940.capstone.util.CreateAndPersist;
import edu.fscj.cen4940.capstone.util.Create;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("AuthController Integration Tests")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CreateAndPersist createAndPersist;

    @Autowired
    private Create create;

    @MockBean
    private EmailService emailService;

    @DisplayName("Register new user with unique email should return HTTP 200 and JWT")
    void register_ValidUser_ShouldReturnOk() throws Exception {
        var request = create.registerRequest("newuser");


        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok").value(true))
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.jwt").exists());

        User created = userRepository.findByEmail(request.email());
        if (created != null) {
            userService.deleteByUsername(created.getUsername());
        }

    }

    @Test
    @DisplayName("Register with existing email should return HTTP 400")
    void register_ExistingEmail_ShouldReturnBadRequest() throws Exception {
        userService.create(createAndPersist.userDTO("existing"));

        var request = create.registerRequest("existing");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.ok").value(false))
                .andExpect(jsonPath("$.message").value("Email already registered"));
    }

    @Test
    @DisplayName("Login with valid credentials should return HTTP 200 and JWT")
    void login_ValidCredentials_ShouldReturnOk() throws Exception {
        UserDTO existingUser = createAndPersist.userDTO("existing");
        userService.create(existingUser);

        var loginRequest = new LoginRequest(
                "existing@example.com",
                "password123"
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok").value(true))
                .andExpect(jsonPath("$.jwt").exists());
    }

    @Test
    @DisplayName("Login with invalid credentials should return HTTP 401")
    void login_InvalidCredentials_ShouldReturnUnauthorized() throws Exception {
        var loginRequest = new LoginRequest(
                "nonexistent@example.com",
                "wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.ok").value(false));
    }
}

