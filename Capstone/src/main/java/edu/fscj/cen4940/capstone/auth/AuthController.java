package edu.fscj.cen4940.capstone.auth;

import edu.fscj.cen4940.capstone.dto.EmailVerificationDTO;
import edu.fscj.cen4940.capstone.dto.UserDTO;
import edu.fscj.cen4940.capstone.entity.User;
import edu.fscj.cen4940.capstone.jwt.util.JwtUtil;
import edu.fscj.cen4940.capstone.repository.UserRepository;
import edu.fscj.cen4940.capstone.service.EmailService;
import edu.fscj.cen4940.capstone.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AuthController {

    private final UserRepository users;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final ApplicationUserDetailsService userDetailsService;
    private final EmailService emailService;
    private final TempStorage tempStorage = TempStorage.getInstance();


    public AuthController(UserRepository users,
                          UserService userService,
                          JwtUtil jwtUtil,
                          ApplicationUserDetailsService userDetailsService,
                          EmailService emailService) {
        this.users = users;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.emailService = emailService;
    }

    public static record RegisterRequest(
            @NotBlank String firstName,
            @NotBlank String lastName,
            @Email @NotBlank String email,
            @NotBlank LocalDate dateOfBirth,
            Integer age,
            Double height,
            Double weight,
            Double goalWeight,
            @NotBlank String password
    ) {}

    public static record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {}

    public static record AuthResponse(boolean ok, String message, Long userId, String firstName, String jwt) {}

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {

        if (users.existsByEmail(req.email().toLowerCase())) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(false, "Email already registered", null, null, null));
        }

        UserDTO savedDTO = userService.create(
                new UserDTO(
                        req.firstName(),
                        req.lastName(),
                        req.email(),
                        req.dateOfBirth(),
                        req.age(),
                        req.weight(), // set initial weight
                        req.weight(),
                        req.goalWeight(),
                        req.height(),
                        req.password()
                )
        );

        // Load the freshly saved DB entity to get ID + hashed credentials
        User entity = userService.searchByEmail(savedDTO.getEmail());

        String token = jwtUtil.generateToken(
                userDetailsService.loadUserByUsername(entity.getEmail())
        );

        return ResponseEntity.ok(new AuthResponse(
                true, "Registered",
                (long) entity.getId(),
                entity.getFirstName(),
                token
        ));
    }




    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) throws Exception {
        User user = userService.searchByEmail(req.email().toLowerCase());

        // If user doesn't exist, immediately return 401
        if (user == null) {
            return ResponseEntity.status(401)
                    .body(new AuthResponse(false, "Invalid credentials", null, null, null));
        }

        if (user.getAccountLockedTime() != null && user.getAccountLockedTime().isAfter(LocalDateTime.now())) {
            System.out.println("Account is locked for " + user.getEmail() + ".");
            // reset lockout timer
            user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(10));
            // If account is locked, return a specific lockout message
            return ResponseEntity.status(401)
                    .body(new AuthResponse(false, "Your account is locked. Please try again later.", null, null, null));
        }

        if (!userService.verifyPassword(req.password(), user.getHash(), user.getSalt())) {
            int failedAttempts = (user.getFailedLoginAttempts() != null) ? user.getFailedLoginAttempts() : 0;
            // Increment failed login attempts
            user.setFailedLoginAttempts(failedAttempts + 1);

            System.out.println("Current failed login attempts for " + user.getEmail() + ": " + user.getFailedLoginAttempts());

            if (user.getFailedLoginAttempts() >= 5) {
                // Lockout for 10 minutes if threshold is reached
                user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(10));
            }

            userService.save(user);
            return ResponseEntity.status(401)
                    .body(new AuthResponse(false, "Invalid credentials", null, null, null));
        }

        user.setFailedLoginAttempts(0);
        user.setAccountLockedUntil(null);
        userService.save(user);

        System.out.println("Current failed login attempts for " + user.getEmail() + ": " + user.getFailedLoginAttempts());

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(
                new AuthResponse(true, "Logged in", (long) user.getId(), user.getFirstName(), token)
        );
    }

    @PostMapping("/send-verification-email")
    public ResponseEntity<?> sendVerificationEmail() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (user.isEmailVerified()) {
            return ResponseEntity.badRequest().body("Email is already verified.");
        }

        // Generate 6-digit code
        String code = String.format("%06d", new Random().nextInt(999999));

        user.setEmailVerificationCode(code);
        userService.save(user);

        // Send the email
        emailService.sendVerificationEmail(user.getEmail(), code);

        return ResponseEntity.ok("Verification email sent.");
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestBody @Valid EmailVerificationDTO dto) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (user.isEmailVerified()) {
            return ResponseEntity.badRequest().body("Email is already verified.");
        }

        if (!dto.getCode().equals(user.getEmailVerificationCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid verification code.");
        }

        user.setEmailVerified(true);
        user.setEmailVerificationCode(null); // clear the code
        userService.save(user);

        return ResponseEntity.ok("Email verified successfully.");
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody @Valid EmailVerificationDTO dto) {
        TempRegistration temp = tempStorage.get(dto.getEmail());
        if (temp == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration expired or invalid.");
        if (!dto.getCode().equals(temp.code()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid verification code.");

        // If code is correct, return ok (but don’t create user yet)
        return ResponseEntity.ok(Map.of("ok", true, "message", "Code verified"));
    }

    @PostMapping("/verify-reset-code")
    public ResponseEntity<?> verifyResetCode(@RequestBody @Valid EmailVerificationDTO dto) {
        TempPasswordReset temp = TempPasswordResetStorage.getInstance().get(dto.getEmail());
        if (temp == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password reset expired or invalid.");
        if (!dto.getCode().equals(temp.code()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid verification code.");

        return ResponseEntity.ok(Map.of("ok", true, "message", "Code verified"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email").toLowerCase();
        String password = body.get("password");

        TempPasswordReset temp = TempPasswordResetStorage.getInstance().get(email);
        if (temp == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password reset expired or invalid.");

        User user = users.findByEmail(email);
        if (user == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found.");

        // Encode password as bytes
        byte[] newSalt = userService.generateSalt();
        byte[] newHash = userService.hashPassword(password, newSalt);

        user.setSalt(newSalt);
        user.setHash(newHash);
        user.setFailedLoginAttempts(0);
        user.setAccountLockedUntil(null);

        userService.save(user);
        System.out.println(user.getEmail() + " has " + user.getFailedLoginAttempts() + " failed logins and " + user.getAccountLockedTime() + " minutes of account lock time");
        TempPasswordResetStorage.getInstance().remove(email);

        return ResponseEntity.ok(Map.of("ok", true, "message", "Password reset successfully."));
    }



    @PostMapping("/pre-register")
    public ResponseEntity<?> preRegister(@Valid @RequestBody RegisterRequest req) {

        if (users.existsByEmail(req.email().toLowerCase())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("ok", false, "message", "Email already registered."));
        }

        String code = String.format("%06d", new Random().nextInt(999999));

        tempStorage.save(
                req.email().toLowerCase(),
                new TempRegistration(req, code)
        );

        emailService.sendVerificationEmail(req.email(), code);

        return ResponseEntity.ok(Map.of("ok", true, "message", "Verification code sent."));
    }


    @PostMapping("/complete-registration")
    public ResponseEntity<?> completeRegistration(@Valid @RequestBody EmailVerificationDTO dto) {
        TempRegistration temp = tempStorage.get(dto.getEmail());

        if (temp == null)
            return ResponseEntity.badRequest().body("Registration expired or invalid.");

        if (!dto.getCode().equals(temp.code()))
            return ResponseEntity.badRequest().body("Invalid verification code.");

        // Create the real user
        UserDTO saved = userService.create(temp.toUserDTO());
        User entity = userService.searchByEmail(saved.getEmail());

        // Generate JWT token
        String token = jwtUtil.generateToken(
                userDetailsService.loadUserByUsername(entity.getEmail())
        );

        // Remove temp registration
        tempStorage.remove(dto.getEmail());

        // Return full AuthResponse so Angular can store token and user info
        return ResponseEntity.ok(new AuthResponse(
                true,
                "Registration complete",
                (long) entity.getId(),
                entity.getFirstName(),
                token
        ));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email").toLowerCase();

        if (!users.existsByEmail(email)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("ok", false, "message", "Unable to send verification code"));
        }

        String code = String.format("%06d", new Random().nextInt(999999));

        TempPasswordResetStorage.getInstance().save(email, new TempPasswordReset(email, code));
        emailService.sendVerificationEmail(email, code);

        return ResponseEntity.ok(Map.of("ok", true, "message", "Verification code sent if the email exists."));
    }


}