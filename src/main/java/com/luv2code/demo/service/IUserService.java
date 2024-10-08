package com.luv2code.demo.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.luv2code.demo.dto.request.ChangePasswordRequestDTO;
import com.luv2code.demo.dto.request.UpdateUserImageRequestDTO;
import com.luv2code.demo.dto.request.UpdateUserProfileRequestDTO;
import com.luv2code.demo.dto.response.ApiResponseDTO;
import com.luv2code.demo.dto.response.UpdateUserProfileResponseDTO;
import com.luv2code.demo.entity.User;

public interface IUserService {

	User getUserTokenDetails(String email);

	void createUser(User user);

	User getUserSetterByEmail(String email);

	ResponseEntity<ApiResponseDTO> UpdatePassword(ChangePasswordRequestDTO changePasswordRequest);

	ResponseEntity<ApiResponseDTO> deleteUser(String email);

	ResponseEntity<Map<String, String>> updateUserImage(UpdateUserImageRequestDTO updateUserImageRequest)
			throws IOException;

	UpdateUserProfileResponseDTO updateUserProfile(UpdateUserProfileRequestDTO updateUserProfileRequest)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException;

}
