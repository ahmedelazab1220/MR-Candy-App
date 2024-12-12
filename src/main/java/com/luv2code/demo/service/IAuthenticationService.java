package com.luv2code.demo.service;

import java.io.IOException;

import org.springframework.http.ResponseEntity;

import com.luv2code.demo.dto.request.LoginRequestDTO;
import com.luv2code.demo.dto.request.RegisterRequestDTO;
import com.luv2code.demo.dto.response.ApiResponseDTO;
import com.luv2code.demo.dto.response.JwtResponseDTO;

public interface IAuthenticationService {

    JwtResponseDTO login(LoginRequestDTO loginRequest);

    ResponseEntity<ApiResponseDTO> register(RegisterRequestDTO registerRequest) throws IOException;

}
