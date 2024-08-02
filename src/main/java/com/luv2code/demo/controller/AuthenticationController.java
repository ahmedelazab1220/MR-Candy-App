package com.luv2code.demo.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.luv2code.demo.dto.request.LoginRequestDTO;
import com.luv2code.demo.dto.request.RegisterRequestDTO;
import com.luv2code.demo.dto.response.ApiResponseDTO;
import com.luv2code.demo.dto.response.JwtResponseDTO;
import com.luv2code.demo.service.IAuthenticationService;
import com.luv2code.demo.service.IRefreshTokenService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("api/v1/auth")
@AllArgsConstructor
public class AuthenticationController {

	private final IAuthenticationService authenticationService;
	private final IRefreshTokenService refreshTokenService;

	@PostMapping("/register")
	public ResponseEntity<ApiResponseDTO> register(@Valid @ModelAttribute RegisterRequestDTO registerRequestDTO) throws IOException{

		return authenticationService.register(registerRequestDTO);

	}

	@PostMapping("/login")
	public ResponseEntity<JwtResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {

		return ResponseEntity.ok(authenticationService.login(loginRequestDTO));

	}

	@PostMapping("/refresh-token")
	public JwtResponseDTO refreshToken(@RequestParam String refreshToken) {

		return refreshTokenService.generateNewToken(refreshToken);

	}

}
