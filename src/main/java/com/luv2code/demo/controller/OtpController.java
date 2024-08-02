package com.luv2code.demo.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.luv2code.demo.dto.request.ChangePasswordRequestDTO;
import com.luv2code.demo.service.IOtpService;

import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/otps")
@AllArgsConstructor
public class OtpController {

	private final IOtpService otpService;
	
	@PostMapping("")
	public ResponseEntity<?> verifyEmail(@RequestParam(required = true) String email)
			throws MessagingException, IOException {

		return otpService.verfiyEmail(email);

	}

	@PostMapping("/verfiy")
	public ResponseEntity<?> verifyOtp(@RequestParam(required = true) String otp,
			@RequestParam(required = true) String email) {

		return otpService.verfiyOtp(otp, email);

	}

	@PostMapping("/pass")
	public ResponseEntity<?> forgetPasswordHandler(@RequestBody ChangePasswordRequestDTO changePasswordRequest) {

		return otpService.forgetPasswordHandler(changePasswordRequest);

	}
	
}
