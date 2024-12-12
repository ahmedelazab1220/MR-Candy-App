package com.luv2code.demo.service;

import java.io.IOException;

import org.springframework.http.ResponseEntity;

import com.luv2code.demo.dto.request.ChangePasswordRequestDTO;
import com.luv2code.demo.dto.response.ApiResponseDTO;

import jakarta.mail.MessagingException;

public interface IOtpService {

    ResponseEntity<ApiResponseDTO> verfiyEmail(String email) throws MessagingException, IOException;

    ResponseEntity<ApiResponseDTO> verfiyOtp(String otp, String email);

    ResponseEntity<ApiResponseDTO> forgetPasswordHandler(ChangePasswordRequestDTO changePasswordRequest);

}
