package com.luv2code.demo.service.impl;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.luv2code.demo.dto.request.ChangePasswordRequestDTO;
import com.luv2code.demo.dto.response.ApiResponseDTO;
import com.luv2code.demo.entity.Otp;
import com.luv2code.demo.entity.User;
import com.luv2code.demo.exc.custom.ExpiredException;
import com.luv2code.demo.exc.custom.NotFoundException;
import com.luv2code.demo.exc.custom.NotFoundTypeException;
import com.luv2code.demo.helper.IOtpGenerator;
import com.luv2code.demo.repository.OtpRepository;
import com.luv2code.demo.service.IEmailService;
import com.luv2code.demo.service.IOtpService;
import com.luv2code.demo.service.IUserService;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OtpService implements IOtpService {

	private final OtpRepository otpRepository;
	private final IUserService userService;
	private final IOtpGenerator otpGenerator;
	private final IEmailService emailService;

	@Transactional
	@Override
	public ResponseEntity<ApiResponseDTO> verfiyEmail(String email) throws MessagingException, IOException {

		User user = userService.getUserSetterByEmail(email);

		String generatedOtp = otpGenerator.generateTOTP();

		Otp otp = Otp.builder().otp(generatedOtp).expirationTime(Instant.now().plus(Duration.ofMinutes(4))).user(user)
				.build();

		otpRepository.save(otp);

		emailService.sendOtpEmail(email, generatedOtp);

		return ResponseEntity.ok(new ApiResponseDTO("Email sent for verification!"));
	}

	@Override
	public ResponseEntity<ApiResponseDTO> verfiyOtp(String otp, String email) {

		Optional<Instant> expirationTime = otpRepository.findExpirationTimeByOtpAndUserEmail(otp, email);

		if (expirationTime.isEmpty()) {
			throw new NotFoundException("Invalid " + NotFoundTypeException.OTP);
		}

		if (expirationTime.get().isBefore(Instant.now())) {
			throw new ExpiredException("Otp Is Expired, If you need new one please click resend!");
		}

		return ResponseEntity.ok(new ApiResponseDTO("OTP verified!"));

	}

	@Override
	public ResponseEntity<ApiResponseDTO> forgetPasswordHandler(ChangePasswordRequestDTO changePasswordRequest) {

		return userService.UpdatePassword(changePasswordRequest);

	}

}
