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

    /**
     * This method is used to set up the test environment before each test. It
     * initializes the Mockito annotations and sets up the test data.
     *
     * @return void
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        setUpTestData();
    }

    /**
     * Sets up the test data for the UserServiceTest class.
     *
     * Initializes the multipartFile, imageUrl, role, address, and user objects
     * with test data.
     *
     * @return void
     */
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

    /**
     * Returns a UserTokenResponseDTO object with test data.
     *
     * @return a UserTokenResponseDTO object with test data
     */
    private UserTokenResponseDTO getUserTokenResponseDTO() {
        return new UserTokenResponseDTO(1L, "ahmed", "ahmed@gmail.com", "01021045629", imageUrl, address, role);
    }

    /**
     * Test case to verify that the getUserTokenDetails method of the
     * UserService returns the expected User object when given a valid email.
     *
     * @param None
     * @return None
     */
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

    /**
     * Test case to verify that the getUserTokenDetails method of the
     * UserService throws a NotFoundException when the user token details are
     * not found in the database.
     *
     * @param None
     * @return None
     */
    @Test
    void shouldThrowNotFoundExceptionWhenUserTokenDetailsNotFound() {
        String email = "sara@gmail.com";

        when(userRepository.findUserTokenDetailsByEmail(email)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.getUserTokenDetails(email));
        assertEquals(USER_NOT_FOUND_MSG, exception.getMessage());
    }

    /**
     * Test case to verify that the getUserTokenDetails method of the
     * UserService throws a RuntimeException when the mapping from
     * UserTokenResponseDTO to User fails.
     *
     * @param None
     * @return None
     */
    @Test
    void shouldThrowExceptionWhenMappingFailsInGetUserTokenDetails() {
        String email = "ahmed@gmail.com";
        UserTokenResponseDTO dto = getUserTokenResponseDTO();

        when(userRepository.findUserTokenDetailsByEmail(email)).thenReturn(Optional.of(dto));
        when(mapper.userTokenResponseDTOTOUser(dto)).thenThrow(new RuntimeException(MAPPING_FAILED_MSG));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.getUserTokenDetails(email));
        assertEquals(MAPPING_FAILED_MSG, exception.getMessage());
    }

    /**
     * Test case to verify that the getUserTokenDetails method of the
     * UserService handles null values correctly.
     *
     * @param None
     * @return None
     */
    @Test
    void shouldHandleNullValuesWhenGetUserTokenDetails() {
        String email = "ahmed@gmail.com";

        when(userRepository.findUserTokenDetailsByEmail(email)).thenReturn(Optional.ofNullable(null));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.getUserTokenDetails(email));
        assertEquals(USER_NOT_FOUND_MSG, exception.getMessage());
    }

    /**
     * Test case to verify that the getUserTokenDetails method of the
     * UserService handles invalid email input correctly and throws an
     * IllegalArgumentException.
     *
     * @param None
     * @return None
     */
    @Test
    void shouldHandleInvalidEmailInputWhenGetUserTokenDetails() {
        String email = "";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.getUserTokenDetails(email));
        assertEquals(EMAIL_EMPTY_MSG, exception.getMessage());
    }

    /**
     * Test case to verify that the createUser method of the UserService is
     * called successfully.
     *
     * @param None
     * @return None
     */
    @Test
    void shouldCreateUserSuccessfully() {
        userService.createUser(user);
        verify(userRepository, times(1)).save(user);
    }

    /**
     * Test case to verify that the createUser method of the UserService throws
     * an IllegalArgumentException when attempting to create a user with an
     * email that already exists in the system.
     *
     * @param None
     * @return None
     */
    @Test
    void shouldThrowExceptionWhenEmailAlreadyExistsWhenCreateUser() {
        when(userRepository.existsByEmail(user.getEmail())).thenThrow(new IllegalArgumentException(EMAIL_IN_USE_MSG));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
        assertEquals(EMAIL_IN_USE_MSG, exception.getMessage());

        verify(userRepository, times(0)).save(user);
    }

    /**
     * Test case to verify that the createUser method of the UserService throws
     * an IllegalArgumentException when attempting to create a user with null
     * fields.
     *
     * @param None
     * @return None
     */
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

    /**
     * Test case to verify that the createUser method of the UserService saves a
     * user successfully even when optional fields are null.
     *
     * @param None
     * @return None
     */
    @Test
    void shouldSaveUserWithOptionalNullFields() {
        user.setFullName(null);
        userService.createUser(user);
        verify(userRepository, times(1)).save(user);
    }

    /**
     * Test case to verify that the deleteUser method of the UserService returns
     * a successful response when attempting to delete a user that exists.
     *
     * @param None
     * @return None
     */
    @Test
    void shouldReturnSuccessWhenDeletingUserExists() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        ResponseEntity<ApiResponseDTO> response = userService.deleteUser(user.getEmail());

        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(userRepository, times(1)).delete(user);
        assertEquals(SUCCESS_DELETED_USER_MSG, response.getBody().getMessage());
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
    }

    /**
     * Test case to verify that the deleteUser method of the UserService throws
     * a NotFoundException when attempting to delete a user that does not exist.
     *
     * @param None
     * @return None
     */
    @Test
    void shouldThrowNotFoundExceptionWhenDeletingUserDoesNotExist() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.deleteUser(user.getEmail()));
        assertEquals(USER_NOT_FOUND_MSG, exception.getMessage());

        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(userRepository, never()).delete(any(User.class));
    }

    /**
     * Test case to verify that the updateUserImage method of the UserService
     * returns a successful response when attempting to update a user's image.
     *
     * @param None
     * @return None
     */
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

    /**
     * Test case to verify that the updateUserImage method of the UserService
     * throws an IOException when attempting to upload a user's image fails.
     *
     * @param None
     * @return None
     */
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

    /**
     * Test case to verify that the updateUserImage method of the UserService
     * handles an exception when deleting an old image fails.
     *
     * @param None
     * @return None
     */
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

    /**
     * Test case to verify that the updateUserProfile method of the UserService
     * updates a user's profile successfully.
     *
     * @return None
     */
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

    /**
     * Test case to verify that the updateUserProfile method of the UserService
     * throws a NotFoundException when the user to be updated is not found.
     *
     * @throws NoSuchFieldException if a specified field is not found
     * @throws SecurityException if a security manager exists and its
     * checkPermission method denies access to the reflection
     * @throws IllegalArgumentException if any argument is null or if the
     * underlying constructor is an instance or static initializer
     * @throws IllegalAccessException if this Method object is enforcing Java
     * language access control and the underlying method is inaccessible
     */
    @Test
    void shouldThrowNotFoundExceptionWhenUserProfileUpdateUserNotFound() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        UpdateUserProfileRequest request = new UpdateUserProfileRequest();
        request.setEmail("notfound@gmail.com");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.updateUserProfile(request));
        assertEquals(USER_NOT_FOUND_MSG, exception.getMessage());
    }

    /**
     * Test case to verify that the updateUserProfile method of the UserService
     * handles exceptions when updating a user's profile fails.
     *
     * @throws NoSuchFieldException if a specified field is not found
     * @throws SecurityException if a security manager exists and its
     * checkPermission method denies access to the reflection
     * @throws IllegalArgumentException if any argument is null or if the
     * underlying constructor is an instance or static initializer
     * @throws IllegalAccessException if this Method object is enforcing Java
     * language access control and the underlying method is inaccessible
     */
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
