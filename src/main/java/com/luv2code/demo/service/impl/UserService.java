package com.luv2code.demo.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.luv2code.demo.dto.SystemMapper;
import com.luv2code.demo.dto.response.UserTokenResponseDTO;
import com.luv2code.demo.entity.User;
import com.luv2code.demo.exc.custom.NotFoundException;
import com.luv2code.demo.exc.custom.NotFoundTypeException;
import com.luv2code.demo.repository.UserRepository;
import com.luv2code.demo.service.IUserService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService implements IUserService {

	private final UserRepository userRepository;
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

}
