package com.luv2code.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserProfileRequest {

    private String fullName;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@gmail\\.com$", message = "Invalid email address")
    private String email;

    @NotBlank
    @Pattern(regexp = "^01[0-2,5]{1}[0-9]{8}$", message = "Invalid phone number")
    private String phoneNumber;

    @NotBlank
    private String state;

    @NotBlank
    private String city;

    @NotBlank
    private String street;

    private String zipCode;

}
