package edu.fscj.cen4940.capstone.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class EmailVerificationDTO {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String code;

    // Getter and setter for email
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    // Getter and setter for code
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
}
