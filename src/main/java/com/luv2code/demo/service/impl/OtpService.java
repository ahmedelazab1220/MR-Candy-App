package com.luv2code.demo.service.impl;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class OtpService implements IOtpService {

    private final OtpRepository otpRepository;
    private final IUserService userService;
    private final IOtpGenerator otpGenerator;
    private final IEmailService emailService;

    @Transactional
    @Override
    public ResponseEntity<ApiResponseDTO> verfiyEmail(String email) throws MessagingException, IOException {

        log.info("Starting email verification process for: {}", email);

        User user = userService.getUserSetterByEmail(email);
        log.debug("User retrieved for email verification: {}", user.getEmail());

        String generatedOtp = otpGenerator.generateTOTP();
        log.debug("Generated OTP for email {}: {}", email, generatedOtp);

        Otp otp = Otp.builder().otp(generatedOtp).expirationTime(Instant.now().plus(Duration.ofMinutes(2))).user(user)
                .build();

        otpRepository.save(otp);
        log.info("OTP saved to database for email: {}", email);

        emailService.sendOtpEmail(email, generatedOtp);
        log.info("OTP email sent to: {}", email);

        return ResponseEntity.ok(new ApiResponseDTO("Email sent for verification!"));

    }

    @Override
    public ResponseEntity<ApiResponseDTO> verfiyOtp(String otp, String email) {

        log.info("Starting OTP verification for email: {}", email);

        Optional<Instant> expirationTime = otpRepository.findExpirationTimeByOtpAndUserEmail(otp, email);

        if (expirationTime.isEmpty()) {
            log.warn("Invalid OTP for email: {}", email);
            throw new NotFoundException("Invalid " + NotFoundTypeException.OTP);
        }

        if (expirationTime.get().isBefore(Instant.now())) {
            log.warn("Expired OTP for email: {}", email);
            throw new ExpiredException("Otp Is Expired, If you need new one please click resend!");
        }

        log.info("OTP verified successfully for email: {}", email);
        return ResponseEntity.ok(new ApiResponseDTO("OTP verified!"));

    }

    @Override
    public ResponseEntity<ApiResponseDTO> forgetPasswordHandler(ChangePasswordRequestDTO changePasswordRequest) {

        log.info("Handling forget password request for email: {}", changePasswordRequest.getEmail());
        return userService.UpdatePassword(changePasswordRequest);

    }

}
