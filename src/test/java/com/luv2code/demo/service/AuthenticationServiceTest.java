package com.luv2code.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import com.luv2code.demo.dto.SystemMapper;
import com.luv2code.demo.dto.request.LoginRequestDTO;
import com.luv2code.demo.dto.request.RegisterRequestDTO;
import com.luv2code.demo.dto.response.ApiResponseDTO;
import com.luv2code.demo.dto.response.JwtResponseDTO;
import com.luv2code.demo.entity.Address;
import com.luv2code.demo.entity.RefreshToken;
import com.luv2code.demo.entity.Role;
import com.luv2code.demo.entity.User;
import com.luv2code.demo.helper.IFileHelper;
import com.luv2code.demo.repository.RoleRepository;
import com.luv2code.demo.security.SecurityUser;
import com.luv2code.demo.service.impl.AuthenticationService;

public class AuthenticationServiceTest {

	@InjectMocks
	private AuthenticationService authenticationService;

	@Mock
	private IUserService userService;

	@Mock
	private RoleRepository roleRepository;

	@Mock
	private IFileHelper fileHelper;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private SystemMapper mapper;

	@Mock
	private IJwtService jwtService;

	@Mock
	private IRefreshTokenService refreshTokenService;

	private Role role;

	private User user;
	
	private Address address;

	private MultipartFile multipartFile;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		multipartFile = new MockMultipartFile("image", "image.png", "image/png", "imageContent".getBytes());

		address = new Address(1L, "Mostafa Kamel", "Tanta", "Egypt", "606165");
		
		role = new Role(1L, "USER", LocalDateTime.now());

		user = new User();

		user.setId(1L);
		user.setFullName("ahmed");
		user.setEmail("ahmed@gmail.com");
		user.setPhoneNumber("01021045629");
		user.setImageUrl("http://example.com/image.png");
		user.setAddress(address);
		user.setRole(role);

	}

	/**
	 * Returns a new instance of LoginRequestDTO with the provided email and
	 * password.
	 *
	 * @return a LoginRequestDTO object with the specified email and password
	 */
	LoginRequestDTO getLoginRequestDTO() {
		return new LoginRequestDTO("ahmed@gmail.com", "password");
	}

	RegisterRequestDTO getRegisterRequestDTO() {
		return RegisterRequestDTO.builder().fullName("Ahmed").email("ahmed@gmail.com").password("password")
				.phoneNumber("01021045629").address(address).image(multipartFile).build();
	}

	/**
	 * Test case to verify that a BadCredentialsException is thrown when the login
	 * process fails due to bad credentials.
	 *
	 * @return void
	 */
	@Test
	void shouldThrowBadCredentialsExceptionWhenDoLogin() {

		LoginRequestDTO loginRequest = getLoginRequestDTO();

		doThrow(new BadCredentialsException("Bad credentials")).when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

		BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
			authenticationService.login(loginRequest);
		});

		verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));

		assertEquals("Bad credentials", exception.getMessage());

		verify(userService, times(0)).getUserTokenDetails(loginRequest.getEmail());
		verify(jwtService, times(0)).generateToken(anyString(), any(UserDetails.class));
		verify(jwtService, times(0)).generateRefreshToken(anyString());
		verify(jwtService, times(0)).extractExpiration(anyString());
		verify(refreshTokenService, times(0)).save(any(RefreshToken.class));

	}

	/**
	 * Ensures that the login process for a user is successful by performing a
	 * series of assertions and verifications.
	 * 
	 * This test case is designed to validate the login functionality by mocking the
	 * necessary dependencies and asserting the expected response. It sets up a
	 * valid login request, creates a user entity with necessary details, and
	 * configures mock responses for the authentication manager, user service, JWT
	 * service, and refresh token service. The test verifies that the authentication
	 * manager, user service, JWT service, and refresh token service are called with
	 * the expected arguments. It then asserts that the response is not null and
	 * contains the expected access token and refresh token.
	 *
	 * @return void
	 * @throws Exception if an error occurs during the test execution
	 */
	@Test
	void shouldLoginUserSuccessfully() {

		LoginRequestDTO loginRequest = getLoginRequestDTO();

		RefreshToken refreshToken = new RefreshToken();
		refreshToken.setId(1L);
		refreshToken.setToken("refreshToken");
		refreshToken.setExpireDate(Instant.now().plus(Duration.ofDays(10)));
		refreshToken.setUser(user);

		Authentication authentication = new UsernamePasswordAuthenticationToken(user, null,
				new SecurityUser(user).getAuthorities());

		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
				.thenReturn(authentication);
		when(userService.getUserTokenDetails(loginRequest.getEmail())).thenReturn(user);
		when(jwtService.generateToken(anyString(), any(UserDetails.class))).thenReturn("accessToken");
		when(jwtService.generateRefreshToken(anyString())).thenReturn("refreshToken");
		when(jwtService.extractExpiration(anyString())).thenReturn(Date.from(java.time.Instant.now()));
		when(refreshTokenService.save(any(RefreshToken.class))).thenReturn(refreshToken);

		JwtResponseDTO response = authenticationService.login(loginRequest);

		verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
		verify(userService, times(1)).getUserTokenDetails(loginRequest.getEmail());
		verify(jwtService, times(1)).generateToken(anyString(), any(UserDetails.class));
		verify(jwtService, times(1)).generateRefreshToken(anyString());
		verify(jwtService, times(1)).extractExpiration(anyString());
		verify(refreshTokenService, times(1)).save(any(RefreshToken.class));

		assertNotNull(response);
		assertEquals("accessToken", response.getAccessToken());
		assertEquals("refreshToken", response.getRefreshToken());

	}

	/**
	 * Test case to verify that a user can successfully register.
	 *
	 * @throws IOException if there is an error reading the image file
	 */
	@Test
	void shouldRegisterUserSuccessfully() throws IOException {

		RegisterRequestDTO registerRequestDTO = getRegisterRequestDTO();

		user.setPassword("encodedPassword");
		
		when(roleRepository.findByRole("USER")).thenReturn(Optional.of(role));
		when(fileHelper.uploadFileToFileSystem(multipartFile)).thenReturn("http://example.com/image.png");
		when(passwordEncoder.encode(registerRequestDTO.getPassword())).thenReturn("encodedPassword");
		when(mapper.registerRequestDTOTOUser(any(RegisterRequestDTO.class))).thenReturn(user);
		doNothing().when(userService).createUser(user);

		ResponseEntity<ApiResponseDTO> response = authenticationService.register(registerRequestDTO);

		verify(roleRepository, times(1)).findByRole("USER");
		verify(passwordEncoder, times(1)).encode("password");
		verify(mapper, times(1)).registerRequestDTOTOUser(registerRequestDTO);
		verify(fileHelper, times(1)).uploadFileToFileSystem(multipartFile);
		verify(userService, times(1)).createUser(user);

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Registration successful! Welcome to Mr Candy App.", response.getBody().getMessage());
		assertEquals("encodedPassword", user.getPassword());

	}

	/**
	 * Test case for handling IOException during file upload.
	 *
	 * @throws IOException if an I/O error occurs
	 */
	@Test
	void shouldThrowIOExceptionDuringFileUpload() throws IOException {

		RegisterRequestDTO registerRequestDTO = getRegisterRequestDTO();

		when(roleRepository.findByRole("USER")).thenReturn(Optional.of(role));
		when(fileHelper.uploadFileToFileSystem(multipartFile)).thenThrow(new IOException("File upload failed"));

		IOException exception = assertThrows(IOException.class, () -> {
			authenticationService.register(registerRequestDTO);
		});

		verify(roleRepository, times(1)).findByRole("USER");
		verify(fileHelper, times(1)).uploadFileToFileSystem(multipartFile);

		assertEquals("File upload failed", exception.getMessage());

		verify(passwordEncoder, times(0)).encode("password");
		verify(mapper, times(0)).registerRequestDTOTOUser(registerRequestDTO);
		verify(userService, times(0)).createUser(any(User.class));

	}

	/**
	 * Test case to verify the handling of a mapping failure during user
	 * registration.
	 *
	 * @throws IOException if an I/O error occurs
	 */
	@Test
	void shouldHandleMapperFailureDuringRegistration() throws IOException {

		RegisterRequestDTO registerRequestDTO = getRegisterRequestDTO();
		
		when(roleRepository.findByRole("USER")).thenReturn(Optional.of(role));
		when(fileHelper.uploadFileToFileSystem(multipartFile)).thenReturn("http://example.com/image.png");
		when(passwordEncoder.encode(registerRequestDTO.getPassword())).thenReturn("encodedPassword");
		when(mapper.registerRequestDTOTOUser(registerRequestDTO)).thenThrow(new RuntimeException("Mapping failed"));

		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			authenticationService.register(registerRequestDTO);
		});

		verify(roleRepository, times(1)).findByRole("USER");
		verify(mapper, times(1)).registerRequestDTOTOUser(registerRequestDTO);
		verify(fileHelper, times(1)).uploadFileToFileSystem(multipartFile);
		verify(passwordEncoder, times(1)).encode("password");

		assertEquals("Mapping failed", exception.getMessage());

		verify(userService, times(0)).createUser(any(User.class));

	}

	/**
	 * Test case to verify the handling of a password encoder failure during user
	 * registration.
	 *
	 * @throws IOException if an I/O error occurs
	 */
	@Test
	void shouldHandlePasswordEncoderFailure() throws IOException {

		RegisterRequestDTO registerRequestDTO = getRegisterRequestDTO();

		when(roleRepository.findByRole("USER")).thenReturn(Optional.of(role));
		when(fileHelper.uploadFileToFileSystem(multipartFile)).thenReturn("http://example.com/image.png");
		when(passwordEncoder.encode(registerRequestDTO.getPassword()))
				.thenThrow(new RuntimeException("Password encoding failed"));

		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			authenticationService.register(registerRequestDTO);
		});

		verify(roleRepository, times(1)).findByRole("USER");
		verify(fileHelper, times(1)).uploadFileToFileSystem(multipartFile);
		verify(passwordEncoder, times(1)).encode("password");

		assertEquals("Password encoding failed", exception.getMessage());

		verify(mapper, times(0)).registerRequestDTOTOUser(any(RegisterRequestDTO.class));
		verify(userService, times(0)).createUser(any(User.class));
		
	}

}
