package com.luv2code.demo.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.luv2code.demo.dto.request.ChangePasswordRequestDTO;
import com.luv2code.demo.dto.request.UpdateUserImageRequest;
import com.luv2code.demo.dto.response.ApiResponseDTO;
import com.luv2code.demo.entity.User;

public interface IUserService {

	User getUserTokenDetails(String email);

	void createUser(User user);

	User getUserSetterByEmail(String email);

	ResponseEntity<ApiResponseDTO> UpdatePassword(ChangePasswordRequestDTO changePasswordRequest);

	ResponseEntity<ApiResponseDTO> deleteUser(String email);

	ResponseEntity<Map<String, String>> updateUserImage(UpdateUserImageRequest updateUserImageRequest)
			throws IOException;

	ResponseEntity<Map<String, String>> updateUserProfile(String email, Map<String, String> userUpdates)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException;

}
