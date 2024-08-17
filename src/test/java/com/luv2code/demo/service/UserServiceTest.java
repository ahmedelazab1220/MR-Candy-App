package com.luv2code.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.luv2code.demo.dto.SystemMapper;
import com.luv2code.demo.dto.request.UpdateUserImageRequest;
import com.luv2code.demo.dto.request.UpdateUserProfileRequest;
import com.luv2code.demo.dto.response.ApiResponseDTO;
import com.luv2code.demo.dto.response.UpdateUserProfileResponseDTO;
import com.luv2code.demo.dto.response.UserTokenResponseDTO;
import com.luv2code.demo.entity.Address;
import com.luv2code.demo.entity.Role;
import com.luv2code.demo.entity.User;
import com.luv2code.demo.exc.custom.NotFoundException;
import com.luv2code.demo.helper.IFileHelper;
import com.luv2code.demo.repository.UserRepository;
import com.luv2code.demo.service.impl.UserService;

public class UserServiceTest {

    private static final String USER_NOT_FOUND_MSG = "USER Not Found!";
    private static final String EMAIL_EMPTY_MSG = "Email must not be empty";
    private static final String EMAIL_IN_USE_MSG = "Email is already in use!";
    private static final String REQUIRED_FIELDS_MISSING_MSG = "Required fields are missing!";
    private static final String SUCCESS_DELETED_USER_MSG = "Success Deleted User!";
    private static final String UPLOAD_FAILED_MSG = "Upload failed";
    private static final String DELETE_FAILED_MSG = "Delete failed";
    private static final String MAPPING_FAILED_MSG = "Mapping failed";

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SystemMapper mapper;

    @Mock
    private IFileHelper fileHelper;

    private Role role;
    private User user;
    private Address address;
    private String imageUrl;
    private MultipartFile multipartFile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        setUpTestData();
    }

    private void setUpTestData() {
        multipartFile = new MockMultipartFile("image", "image.png", "image/png", "imageContent".getBytes());
        imageUrl = "http://example.com/image.png";
        role = new Role(1L, "USER", LocalDateTime.now());
        address = new Address(1L, "Mostafa Kamel", "Tanta", "Egypt", "606165");

        user = new User();
        user.setId(1L);
        user.setFullName("ahmed");
        user.setEmail("ahmed@gmail.com");
        user.setPassword("12345678");
        user.setPhoneNumber("01021045629");
        user.setImageUrl(imageUrl);
        user.setAddress(address);
        user.setRole(role);
    }

    private UserTokenResponseDTO getUserTokenResponseDTO() {
        return new UserTokenResponseDTO(1L, "ahmed", "ahmed@gmail.com", "01021045629", imageUrl, address, role);
    }

    @Test
    void shouldGetUserTokenDetailsSuccessfully() {
        String email = "ahmed@gmail.com";
        UserTokenResponseDTO dto = getUserTokenResponseDTO();

        when(userRepository.findUserTokenDetailsByEmail(email)).thenReturn(Optional.of(dto));
        when(mapper.userTokenResponseDTOTOUser(dto)).thenReturn(user);

        User result = userService.getUserTokenDetails(email);

        assertNotNull(result);
        assertEquals("ahmed", result.getFullName());
        assertEquals("ahmed@gmail.com", result.getEmail());
        assertEquals("01021045629", result.getPhoneNumber());
        assertEquals(imageUrl, result.getImageUrl());
        assertEquals(role, result.getRole());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserTokenDetailsNotFound() {
        String email = "sara@gmail.com";

        when(userRepository.findUserTokenDetailsByEmail(email)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.getUserTokenDetails(email));
        assertEquals(USER_NOT_FOUND_MSG, exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenMappingFailsInGetUserTokenDetails() {
        String email = "ahmed@gmail.com";
        UserTokenResponseDTO dto = getUserTokenResponseDTO();

        when(userRepository.findUserTokenDetailsByEmail(email)).thenReturn(Optional.of(dto));
        when(mapper.userTokenResponseDTOTOUser(dto)).thenThrow(new RuntimeException(MAPPING_FAILED_MSG));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.getUserTokenDetails(email));
        assertEquals(MAPPING_FAILED_MSG, exception.getMessage());
    }

    @Test
    void shouldHandleNullValuesWhenGetUserTokenDetails() {
        String email = "ahmed@gmail.com";

        when(userRepository.findUserTokenDetailsByEmail(email)).thenReturn(Optional.ofNullable(null));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.getUserTokenDetails(email));
        assertEquals(USER_NOT_FOUND_MSG, exception.getMessage());
    }

    @Test
    void shouldHandleInvalidEmailInputWhenGetUserTokenDetails() {
        String email = "";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.getUserTokenDetails(email));
        assertEquals(EMAIL_EMPTY_MSG, exception.getMessage());
    }

    @Test
    void shouldCreateUserSuccessfully() {
        userService.createUser(user);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExistsWhenCreateUser() {
        when(userRepository.existsByEmail(user.getEmail())).thenThrow(new IllegalArgumentException(EMAIL_IN_USE_MSG));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
        assertEquals(EMAIL_IN_USE_MSG, exception.getMessage());

        verify(userRepository, times(0)).save(user);
    }

    @Test
    void shouldThrowExceptionWhenCreatingUserWithNullFields() {
        User invalidUser = new User();

        assertThrows(IllegalArgumentException.class, () -> {
            if (invalidUser.getEmail() == null || invalidUser.getPassword() == null || invalidUser.getImageUrl() == null
                    || invalidUser.getPhoneNumber() == null || invalidUser.getRole() == null
                    || invalidUser.getAddress() == null) {
                throw new IllegalArgumentException(REQUIRED_FIELDS_MISSING_MSG);
            }
            userService.createUser(invalidUser);
        });

        verify(userRepository, times(0)).save(user);
    }

    @Test
    void shouldSaveUserWithOptionalNullFields() {
        user.setFullName(null);
        userService.createUser(user);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void shouldReturnSuccessWhenDeletingUserExists() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        ResponseEntity<ApiResponseDTO> response = userService.deleteUser(user.getEmail());

        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(userRepository, times(1)).delete(user);
        assertEquals(SUCCESS_DELETED_USER_MSG, response.getBody().getMessage());
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenDeletingUserDoesNotExist() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.deleteUser(user.getEmail()));
        assertEquals(USER_NOT_FOUND_MSG, exception.getMessage());

        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void shouldUpdateUserImageSuccessfully() throws IOException {
        UpdateUserImageRequest request = new UpdateUserImageRequest();
        request.setEmail("ahmed@gmail.com");
        request.setOldImageUrl(imageUrl);
        request.setImage(multipartFile);

        when(fileHelper.uploadFileToFileSystem(request.getImage())).thenReturn(imageUrl);
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));

        ResponseEntity<Map<String, String>> response = userService.updateUserImage(request);

        verify(fileHelper, times(1)).deleteImageFromFileSystem(request.getOldImageUrl());
        verify(fileHelper, times(1)).uploadFileToFileSystem(request.getImage());
        verify(userRepository, times(1)).updateImageByEmail(request.getEmail(), imageUrl);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertEquals(imageUrl, response.getBody().get("imageUrl"));
    }

    @Test
    void shouldThrowExceptionWhenImageUploadFails() throws IOException {
        UpdateUserImageRequest request = new UpdateUserImageRequest();
        request.setEmail("ahmed@gmail.com");
        request.setOldImageUrl(imageUrl);
        request.setImage(multipartFile);

        when(fileHelper.uploadFileToFileSystem(request.getImage())).thenThrow(new IOException(UPLOAD_FAILED_MSG));
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));

        IOException exception = assertThrows(IOException.class, () -> userService.updateUserImage(request));
        assertEquals(UPLOAD_FAILED_MSG, exception.getMessage());

        verify(fileHelper, times(1)).deleteImageFromFileSystem(request.getOldImageUrl());
        verify(userRepository, times(0)).updateImageByEmail(request.getEmail(), null);
    }

    @Test
    void shouldHandleExceptionWhenDeletingOldImageFails() throws IOException {
        UpdateUserImageRequest request = new UpdateUserImageRequest();
        request.setEmail("ahmed@gmail.com");
        request.setOldImageUrl(imageUrl);
        request.setImage(multipartFile);

        when(fileHelper.deleteImageFromFileSystem(request.getOldImageUrl())).thenThrow(new IOException(DELETE_FAILED_MSG));
        when(fileHelper.uploadFileToFileSystem(request.getImage())).thenReturn(imageUrl);
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));

        IOException exception = assertThrows(IOException.class, () -> userService.updateUserImage(request));
        assertEquals(DELETE_FAILED_MSG, exception.getMessage());

        verify(userRepository, times(0)).updateImageByEmail(request.getEmail(), null);
    }

    @Test
    void shouldUpdateUserProfileSuccessfully() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        UpdateUserProfileRequest request = new UpdateUserProfileRequest();
        request.setEmail("ahmed@gmail.com");
        request.setCity("Los Angeles");
        request.setState("California");
        request.setStreet("Main St");
        request.setZipCode("90001");

        UpdateUserProfileResponseDTO expectedResponse = new UpdateUserProfileResponseDTO(
                user.getFullName(), user.getPhoneNumber(), request.getState(),
                request.getCity(), request.getStreet(), request.getZipCode()
        );

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(mapper.updateUserProfileRequestTOUser(request, user)).thenReturn(user);
        when(mapper.updateUserProfileRequestTOUpdateUserProfileResponse(request)).thenReturn(expectedResponse);

        UpdateUserProfileResponseDTO response = userService.updateUserProfile(request);

        verify(userRepository, times(1)).findByEmail(request.getEmail());
        verify(mapper, times(1)).updateUserProfileRequestTOUser(request, user);
        verify(userRepository, times(1)).save(user);

        assertEquals(expectedResponse, response);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserProfileUpdateUserNotFound() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        UpdateUserProfileRequest request = new UpdateUserProfileRequest();
        request.setEmail("notfound@gmail.com");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.updateUserProfile(request));
        assertEquals(USER_NOT_FOUND_MSG, exception.getMessage());
    }

    @Test
    void shouldHandleExceptionWhenUpdatingUserProfileFails() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        UpdateUserProfileRequest request = new UpdateUserProfileRequest();
        request.setEmail("ahmed@gmail.com");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        doThrow(new IllegalArgumentException(MAPPING_FAILED_MSG)).when(mapper).updateUserProfileRequestTOUser(request, user);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.updateUserProfile(request));
        assertEquals(MAPPING_FAILED_MSG, exception.getMessage());

        verify(userRepository, times(1)).findByEmail(request.getEmail());
        verify(userRepository, never()).save(user);
    }
}
