package com.luv2code.demo.dto.request;

import org.springframework.web.multipart.MultipartFile;

import com.luv2code.demo.entity.Address;
import com.luv2code.demo.entity.Role;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequestDTO {

	private String fullName;

	@Pattern(regexp = "^[a-zA-Z0-9._%+-]+@gmail\\.com$", message = "Invalid email address")
	private String email;

	@Size(min = 8, message = "Password must be at least 8 characters long")
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).*$", message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, and one special character. No whitespace allowed.")
	private String password;

	@Pattern(regexp = "^01[0-2,5]{1}[0-9]{8}$", message = "Invalid phone number")
	private String phoneNumber;

	@NotNull
	private MultipartFile image;
	
	@NotNull
	private Address address;

	private Role role;

}
