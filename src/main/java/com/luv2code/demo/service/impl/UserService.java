package com.luv2code.demo.service.impl;

import java.util.Objects;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.luv2code.demo.dto.SystemMapper;
import com.luv2code.demo.dto.UserSetterDTO;
import com.luv2code.demo.dto.request.ChangePasswordRequestDTO;
import com.luv2code.demo.dto.response.ApiResponseDTO;
import com.luv2code.demo.dto.response.UserTokenResponseDTO;
import com.luv2code.demo.entity.User;
import com.luv2code.demo.exc.custom.NotFoundException;
import com.luv2code.demo.exc.custom.NotFoundTypeException;
import com.luv2code.demo.repository.UserRepository;
import com.luv2code.demo.service.IUserService;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService implements IUserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final SystemMapper mapper;

	@Override
	public User getUserTokenDetails(String email) {

		if (email.isEmpty()) {
			throw new IllegalArgumentException("Email must not be empty");
		}

		Optional<UserTokenResponseDTO> userToken = userRepository.findUserTokenDetailsByEmail(email);

		if (!userToken.isPresent()) {
			throw new NotFoundException(NotFoundTypeException.USER + " Not Found!");
		}

		return mapper.userTokenResponseDTOTOUser(userToken.get());

	}

	@Override
	public void createUser(User user) {

		if (user.getEmail() == null || user.getPassword() == null || user.getImageUrl() == null
				|| user.getPhoneNumber() == null || user.getRole() == null || user.getAddress() == null) {
			throw new IllegalArgumentException("Required fields are missing!");
		}

		Boolean userIsExist = userRepository.existsByEmail(user.getEmail());

		if (userIsExist) {
			throw new IllegalArgumentException("Email is already in use!");
		}

		userRepository.save(user);

	}

	@Override
	public User getUserSetterByEmail(String email) {

		Optional<UserSetterDTO> userSetterDTO = userRepository.findUserSetterByEmail(email);

		if (userSetterDTO.isEmpty()) {
			throw new NotFoundException(NotFoundTypeException.USER + " Not Found!");
		}

		UserSetterDTO userDto = userSetterDTO.get();

		return mapper.userSetterDTOTOUser(userDto);

	}

	@Transactional
	@Override
	public ResponseEntity<ApiResponseDTO> UpdatePassword(ChangePasswordRequestDTO changePasswordRequest) {

		if (changePasswordRequest.getOldPassword() != null) {
			Optional<String> pass = userRepository.findUserPasswordByEmail(changePasswordRequest.getEmail());

			if (pass.isEmpty()) {
				throw new NotFoundException(NotFoundTypeException.USER + " Not Found!");
			}

			if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), pass.get())) {
				throw new IllegalArgumentException("Old password is incorrect!");
			}

		}

		if (!Objects.equals(changePasswordRequest.getNewPassword(), changePasswordRequest.getNewRepeatedPassword())) {
			return ResponseEntity.ok(
					new ApiResponseDTO("Password not equal confirmation password,Please enter the password again!"));
		}

		Integer updateRows = userRepository.updatePasswordByEmail(changePasswordRequest.getEmail(),
				passwordEncoder.encode(changePasswordRequest.getNewPassword()));

		if (updateRows == 0) {
			throw new NotFoundException("password not change ,please try later!");
		}

		return ResponseEntity.ok(new ApiResponseDTO("Password has been changed!"));

	}

}
