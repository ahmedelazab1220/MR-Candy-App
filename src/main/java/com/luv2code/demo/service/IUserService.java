package com.luv2code.demo.service;

import org.springframework.http.ResponseEntity;

import com.luv2code.demo.dto.request.ChangePasswordRequestDTO;
import com.luv2code.demo.dto.response.ApiResponseDTO;
import com.luv2code.demo.entity.User;

public interface IUserService {

	User getUserTokenDetails(String email);

	void createUser(User user);

	User getUserSetterByEmail(String email);

	ResponseEntity<ApiResponseDTO> UpdatePassword(ChangePasswordRequestDTO changePasswordRequest);
}
