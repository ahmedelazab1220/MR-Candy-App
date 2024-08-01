package com.luv2code.demo.service.impl;

import java.io.IOException;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.luv2code.demo.dto.SystemMapper;
import com.luv2code.demo.dto.request.LoginRequestDTO;
import com.luv2code.demo.dto.request.RegisterRequestDTO;
import com.luv2code.demo.dto.response.ApiResponseDTO;
import com.luv2code.demo.dto.response.JwtResponseDTO;
import com.luv2code.demo.entity.RefreshToken;
import com.luv2code.demo.entity.User;
import com.luv2code.demo.exc.custom.NotFoundException;
import com.luv2code.demo.exc.custom.NotFoundTypeException;
import com.luv2code.demo.helper.IFileHelper;
import com.luv2code.demo.repository.RoleRepository;
import com.luv2code.demo.security.SecurityUser;
import com.luv2code.demo.service.IAuthenticationService;
import com.luv2code.demo.service.IJwtService;
import com.luv2code.demo.service.IRefreshTokenService;
import com.luv2code.demo.service.IUserService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthenticationService implements IAuthenticationService {

	private final IUserService userService;
	private final RoleRepository roleRepository;
	private final IFileHelper fileHelper;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final SystemMapper mapper;
	private final IJwtService jwtService;
	private final IRefreshTokenService refreshTokenService;

	@Override
	public JwtResponseDTO login(LoginRequestDTO loginRequestDTO) {

		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(), loginRequestDTO.getPassword()));

		Optional<User> user = Optional.ofNullable(userService.getUserTokenDetails(loginRequestDTO.getEmail()));

		if (user.isEmpty()) {
			throw new NotFoundException(NotFoundTypeException.USER + " Not Found!");
		}

		String accessToken = jwtService.generateToken(loginRequestDTO.getEmail(), user.map(SecurityUser::new).get());
		String refreshToken = jwtService.generateRefreshToken(loginRequestDTO.getEmail());

		RefreshToken refresh_token = RefreshToken.builder().token(refreshToken).user(user.get())
				.expireDate(jwtService.extractExpiration(refreshToken).toInstant()).build();

		refreshTokenService.save(refresh_token);

		return createJwtResponse(accessToken, refreshToken);

	}

	@Override
	public ResponseEntity<ApiResponseDTO> register(RegisterRequestDTO registerRequestDTO) throws IOException {

		if (registerRequestDTO.getRole() == null) {
			registerRequestDTO.setRole(roleRepository.findByRole("USER").get());
		}

		String imageUrl = fileHelper.uploadFileToFileSystem(registerRequestDTO.getImage());

		registerRequestDTO.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
		
		User user = mapper.registerRequestDTOTOUser(registerRequestDTO);

		user.setImageUrl(imageUrl);

		userService.createUser(user);

		return ResponseEntity.ok(new ApiResponseDTO("Registration successful! Welcome to Mr Candy App."));

	}

	private JwtResponseDTO createJwtResponse(String accessToken, String refreshToken) {

		return new JwtResponseDTO(accessToken, refreshToken);

	}

}
