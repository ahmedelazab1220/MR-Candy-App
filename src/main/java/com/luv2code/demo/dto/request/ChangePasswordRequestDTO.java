package com.luv2code.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChangePasswordRequestDTO {

    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@gmail\\.com$", message = "Invalid email address")
    @NotBlank
    private String email;

    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).*$", message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, and one special character. No whitespace allowed.")
    @NotBlank
    private String oldPassword;

    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).*$", message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, and one special character. No whitespace allowed.")
    @NotBlank
    private String newPassword;

    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).*$", message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, and one special character. No whitespace allowed.")
    @NotBlank
    private String newRepeatedPassword;

}
