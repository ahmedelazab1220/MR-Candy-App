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

    private static final String TEST_EMAIL = "ahmed@gmail.com";
    private static final String TEST_PASSWORD = "password";
    private static final String TEST_ROLE = "USER";
    private static final String TEST_IMAGE_URL = "http://example.com/image.png";
    private static final String BAD_CREDENTIALS_MESSAGE = "Bad credentials";
    private static final String REGISTRATION_SUCCESS_MESSAGE = "Registration successful! Welcome to Mr Candy App.";
    private static final String FILE_UPLOAD_FAILED_MESSAGE = "File upload failed";
    private static final String MAPPING_FAILED_MESSAGE = "Mapping failed";
    private static final String PASSWORD_ENCODING_FAILED_MESSAGE = "Password encoding failed";
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String REFRESH_TOKEN = "refreshToken";

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

        role = new Role(1L, TEST_ROLE, LocalDateTime.now());

        user = new User();

        user.setId(1L);
        user.setFullName("ahmed");
        user.setEmail(TEST_EMAIL);
        user.setPhoneNumber("01021045629");
        user.setImageUrl(TEST_IMAGE_URL);
        user.setAddress(address);
        user.setRole(role);

    }

    LoginRequestDTO getLoginRequestDTO() {
        return new LoginRequestDTO(TEST_EMAIL, TEST_PASSWORD);
    }

    RegisterRequestDTO getRegisterRequestDTO() {
        return RegisterRequestDTO.builder().fullName("Ahmed").email(TEST_EMAIL).password(TEST_PASSWORD)
                .phoneNumber("01021045629").address(address).image(multipartFile).build();
    }

    @Test
    void shouldThrowBadCredentialsExceptionWhenDoLogin() {

        LoginRequestDTO loginRequest = getLoginRequestDTO();

        doThrow(new BadCredentialsException(BAD_CREDENTIALS_MESSAGE)).when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            authenticationService.login(loginRequest);
        });

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertEquals(BAD_CREDENTIALS_MESSAGE, exception.getMessage());

        verify(userService, times(0)).getUserTokenDetails(loginRequest.getEmail());
        verify(jwtService, times(0)).generateToken(anyString(), any(UserDetails.class));
        verify(jwtService, times(0)).generateRefreshToken(anyString());
        verify(jwtService, times(0)).extractExpiration(anyString());
        verify(refreshTokenService, times(0)).save(any(RefreshToken.class));

    }

    @Test
    void shouldLoginUserSuccessfully() {

        LoginRequestDTO loginRequest = getLoginRequestDTO();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(1L);
        refreshToken.setToken(REFRESH_TOKEN);
        refreshToken.setExpireDate(Instant.now().plus(Duration.ofDays(10)));
        refreshToken.setUser(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null,
                new SecurityUser(user).getAuthorities());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userService.getUserTokenDetails(loginRequest.getEmail())).thenReturn(user);
        when(jwtService.generateToken(anyString(), any(UserDetails.class))).thenReturn(ACCESS_TOKEN);
        when(jwtService.generateRefreshToken(anyString())).thenReturn(REFRESH_TOKEN);
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
        assertEquals(ACCESS_TOKEN, response.getAccessToken());
        assertEquals(REFRESH_TOKEN, response.getRefreshToken());

    }

    @Test
    void shouldRegisterUserSuccessfully() throws IOException {

        RegisterRequestDTO registerRequestDTO = getRegisterRequestDTO();

        user.setPassword("encodedPassword");

        when(roleRepository.findByRole(TEST_ROLE)).thenReturn(Optional.of(role));
        when(fileHelper.uploadFileToFileSystem(multipartFile)).thenReturn(TEST_IMAGE_URL);
        when(passwordEncoder.encode(registerRequestDTO.getPassword())).thenReturn("encodedPassword");
        when(mapper.registerRequestDTOTOUser(any(RegisterRequestDTO.class))).thenReturn(user);
        doNothing().when(userService).createUser(user);

        ResponseEntity<ApiResponseDTO> response = authenticationService.register(registerRequestDTO);

        verify(roleRepository, times(1)).findByRole(TEST_ROLE);
        verify(passwordEncoder, times(1)).encode(TEST_PASSWORD);
        verify(mapper, times(1)).registerRequestDTOTOUser(registerRequestDTO);
        verify(fileHelper, times(1)).uploadFileToFileSystem(multipartFile);
        verify(userService, times(1)).createUser(user);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(REGISTRATION_SUCCESS_MESSAGE, response.getBody().getMessage());
        assertEquals("encodedPassword", user.getPassword());

    }

    @Test
    void shouldThrowIOExceptionDuringFileUpload() throws IOException {

        RegisterRequestDTO registerRequestDTO = getRegisterRequestDTO();

        when(roleRepository.findByRole(TEST_ROLE)).thenReturn(Optional.of(role));
        when(fileHelper.uploadFileToFileSystem(multipartFile)).thenThrow(new IOException(FILE_UPLOAD_FAILED_MESSAGE));

        IOException exception = assertThrows(IOException.class, () -> {
            authenticationService.register(registerRequestDTO);
        });

        verify(roleRepository, times(1)).findByRole(TEST_ROLE);
        verify(fileHelper, times(1)).uploadFileToFileSystem(multipartFile);

        assertEquals(FILE_UPLOAD_FAILED_MESSAGE, exception.getMessage());

        verify(passwordEncoder, times(0)).encode(TEST_PASSWORD);
        verify(mapper, times(0)).registerRequestDTOTOUser(registerRequestDTO);
        verify(userService, times(0)).createUser(any(User.class));

    }

    @Test
    void shouldHandleMapperFailureDuringRegistration() throws IOException {

        RegisterRequestDTO registerRequestDTO = getRegisterRequestDTO();

        when(roleRepository.findByRole(TEST_ROLE)).thenReturn(Optional.of(role));
        when(fileHelper.uploadFileToFileSystem(multipartFile)).thenReturn(TEST_IMAGE_URL);
        when(passwordEncoder.encode(registerRequestDTO.getPassword())).thenReturn("encodedPassword");
        when(mapper.registerRequestDTOTOUser(registerRequestDTO)).thenThrow(new RuntimeException(MAPPING_FAILED_MESSAGE));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authenticationService.register(registerRequestDTO);
        });

        verify(roleRepository, times(1)).findByRole(TEST_ROLE);
        verify(mapper, times(1)).registerRequestDTOTOUser(registerRequestDTO);
        verify(fileHelper, times(1)).uploadFileToFileSystem(multipartFile);
        verify(passwordEncoder, times(1)).encode(TEST_PASSWORD);

        assertEquals(MAPPING_FAILED_MESSAGE, exception.getMessage());

        verify(userService, times(0)).createUser(any(User.class));

    }

    @Test
    void shouldHandlePasswordEncoderFailure() throws IOException {

        RegisterRequestDTO registerRequestDTO = getRegisterRequestDTO();

        when(roleRepository.findByRole(TEST_ROLE)).thenReturn(Optional.of(role));
        when(fileHelper.uploadFileToFileSystem(multipartFile)).thenReturn(TEST_IMAGE_URL);
        when(passwordEncoder.encode(registerRequestDTO.getPassword()))
                .thenThrow(new RuntimeException(PASSWORD_ENCODING_FAILED_MESSAGE));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authenticationService.register(registerRequestDTO);
        });

        verify(roleRepository, times(1)).findByRole(TEST_ROLE);
        verify(fileHelper, times(1)).uploadFileToFileSystem(multipartFile);
        verify(passwordEncoder, times(1)).encode(TEST_PASSWORD);

        assertEquals(PASSWORD_ENCODING_FAILED_MESSAGE, exception.getMessage());

        verify(mapper, times(0)).registerRequestDTOTOUser(any(RegisterRequestDTO.class));
        verify(userService, times(0)).createUser(any(User.class));

    }

}
