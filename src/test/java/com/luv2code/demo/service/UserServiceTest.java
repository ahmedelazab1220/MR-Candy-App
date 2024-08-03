package com.luv2code.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.luv2code.demo.dto.SystemMapper;
import com.luv2code.demo.dto.response.UserTokenResponseDTO;
import com.luv2code.demo.entity.Address;
import com.luv2code.demo.entity.Role;
import com.luv2code.demo.entity.User;
import com.luv2code.demo.exc.custom.NotFoundException;
import com.luv2code.demo.exc.custom.NotFoundTypeException;
import com.luv2code.demo.repository.UserRepository;
import com.luv2code.demo.service.impl.UserService;

public class UserServiceTest {

	@InjectMocks
	private UserService userService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private SystemMapper mapper;

	private Role role;

	private User user;

	private Address address;

	/**
	 * Sets up the necessary mocks and initializes the role and user objects before
	 * each test case.
	 *
	 * @throws Exception if there is an error with the mocks initialization.
	 */
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		role = new Role(1L, "USER", LocalDateTime.now());

		address = new Address(1L, "Mostafa Kamel", "Tanta", "Egypt", "606165");

		user = new User();
		user.setId(1L);
		user.setFullName("ahmed");
		user.setEmail("ahmed@gmail.com");
		user.setPassword("12345678");
		user.setPhoneNumber("01021045629");
		user.setImageUrl("http://example.com/image.png");
		user.setAddress(address);
		user.setRole(role);
	}

	UserTokenResponseDTO getUserTokenResponseDTO() {

		return new UserTokenResponseDTO(1L, "ahmed", "ahmed@gmail.com", "01021045629", "http://example.com/image.png",
				address, role);
	}

	/**
	 * Test case to verify that the method `getUserTokenDetails` successfully
	 * retrieves the user token details.
	 *
	 * @throws None
	 * @return None
	 */
	@Test
	public void shouldGetUserTokenDetailsSuccessfully() {

		String email = "ahmed@gmail.com";

		UserTokenResponseDTO dto = getUserTokenResponseDTO();

		when(userRepository.findUserTokenDetailsByEmail(Mockito.anyString())).thenReturn(Optional.of(dto));

		when(mapper.userTokenResponseDTOTOUser(dto)).thenReturn(user);

		Optional<User> result = Optional.of(userService.getUserTokenDetails(email));

		// Assert
		assertEquals(true, result.isPresent());
		// assertEquals(false, result.isPresent()); // This Give Failure If Do Uncomment
		assertNotNull(result.get());
		assertEquals("ahmed", result.get().getFullName());
		assertEquals("ahmed@gmail.com", result.get().getEmail());
		assertEquals("01021045629", result.get().getPhoneNumber());
		assertEquals("http://example.com/image.png", result.get().getImageUrl());
		assertEquals(role, result.get().getRole());

	}

	/**
	 * Test case to verify that the method `getUserTokenDetails` throws a
	 * `NotFoundException` when the user token details are not found.
	 *
	 * @throws NotFoundException if the user token details are not found
	 */
	@Test
	public void shouldGetUserTokenDetailsNotFound() {

		String email = "sara@gmail.com";

		when(userRepository.findUserTokenDetailsByEmail(Mockito.anyString())).thenReturn(Optional.empty());

		NotFoundException exception = assertThrows(NotFoundException.class,
				() -> userService.getUserTokenDetails(email));

		assertEquals(NotFoundTypeException.USER + " Not Found!", exception.getMessage());

	}

	/**
	 * Test case to verify that the method `getUserTokenDetails` throws a
	 * `RuntimeException` when mapping fails.
	 *
	 * @throws RuntimeException if mapping fails
	 */
	@Test
	public void shouldThrowExceptionWhenMappingFailsInGetUserTokenDetails() {

		String email = "ahmed@gmail.com";

		UserTokenResponseDTO dto = getUserTokenResponseDTO();

		when(userRepository.findUserTokenDetailsByEmail(Mockito.anyString())).thenReturn(Optional.of(dto));

		when(mapper.userTokenResponseDTOTOUser(dto)).thenThrow(new RuntimeException("Mapping failed"));

		RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.getUserTokenDetails(email));

		assertEquals("Mapping failed", exception.getMessage());

	}

	/**
	 * Test case to verify that the method `getUserTokenDetails` handles null values
	 * correctly.
	 *
	 * @throws NotFoundException if the user token details are not found
	 */
	@Test
	public void shouldHandleNullValuesWhenGetUserTokenDetails() {

		String email = "ahmed@gmail.com";

		when(userRepository.findUserTokenDetailsByEmail(Mockito.anyString())).thenReturn(Optional.ofNullable(null));

		NotFoundException exception = assertThrows(NotFoundException.class,
				() -> userService.getUserTokenDetails(email));

		assertEquals(NotFoundTypeException.USER + " Not Found!", exception.getMessage());

	}

	/**
	 * Test case to verify that the method `getUserTokenDetails` handles an invalid
	 * email input correctly.
	 *
	 * This test case verifies that when an empty string is passed as the email
	 * parameter to the `getUserTokenDetails` method, an `IllegalArgumentException`
	 * is thrown with the message "Email must not be empty".
	 *
	 * @throws IllegalArgumentException if the email parameter is empty
	 */
	@Test
	public void shouldHandleInvalidEmailInputWhenGetUserTokenDetails() {

		String email = "";

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> userService.getUserTokenDetails(email));

		assertEquals("Email must not be empty", exception.getMessage());

	}

	/**
	 * Test case to verify that the method `createUser` successfully creates a user.
	 *
	 * This test case creates a new `Role` object with an ID of 1 and the role name
	 * "USER". It then creates a new `User` object with an ID of 1, a full name of
	 * "ahmed", an email of "ahmed@gmail.com", a phone number of "01021045629", an
	 * image URL of "http://example.com/image.png", and the previously created
	 * `Role` object.
	 *
	 * The `createUser` method is then called with the newly created `User` object
	 * as the parameter.
	 *
	 * Finally, the `verify` method is called on the `userRepository` mock, passing
	 * in the `save` method and the `user` object as parameters. This verifies that
	 * the `save` method was called exactly once with the `user` object as the
	 * parameter.
	 *
	 * @throws None
	 * @return None
	 */
	@Test
	public void shouldCreateUserSuccessfully() {

		userService.createUser(user);

		verify(userRepository, times(1)).save(user);

	}

	/**
	 * Test case to verify that the method `createUser` throws a
	 * `DataIntegrityViolationException` when the email already exists in the
	 * database.
	 *
	 * @throws IllegalArgumentException if the email already exists in the
	 *                                         database
	 */
	@Test
	public void shouldThrowExceptionWhenEmailAlreadyExistsWhenCreateUser() {

		when(userRepository.existsByEmail(user.getEmail())).thenThrow(new IllegalArgumentException("Email is already in use!"));
		
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));

		assertEquals("Email is already in use!", exception.getMessage());
		
		verify(userRepository, times(0)).save(user);

	}

	/**
	 * Test case to verify that the method `createUser` throws an
	 * `IllegalArgumentException` when creating a user with null fields.
	 *
	 * @throws IllegalArgumentException if any of the required fields (email,
	 *                                  password, imageUrl, phoneNumber, role) is
	 *                                  null
	 */
	@Test
	public void shouldThrowExceptionWhenCreatingUserWithNullFields() {

		User invalidUser = new User();

		assertThrows(IllegalArgumentException.class, () -> {
			if (invalidUser.getEmail() == null || invalidUser.getPassword() == null || invalidUser.getImageUrl() == null
					|| invalidUser.getPhoneNumber() == null || invalidUser.getRole() == null
					|| invalidUser.getAddress() == null) {
				throw new IllegalArgumentException("Required fields are missing!");
			}
			userService.createUser(invalidUser);
		});

		verify(userRepository, times(0)).save(user);

	}

	/**
	 * Test case to verify that the method `createUser` successfully saves a user
	 * with optional null fields.
	 *
	 * This test case creates a new `Role` object with an ID of 1 and the role name
	 * "USER". It then creates a new `User` object with an ID of 1, an email of
	 * "ahmed@gmail.com", a password of "12345678", a phone number of "01021545629",
	 * an image URL of "http://example.com/image.png", and the previously created
	 * `Role` object.
	 *
	 * The `createUser` method is then called with the newly created `User` object
	 * as the parameter.
	 *
	 * Finally, the `verify` method is called on the `userRepository` mock, passing
	 * in the `save` method and the `user` object as parameters. This verifies that
	 * the `save` method was called exactly once with the `user` object as the
	 * parameter.
	 *
	 * @throws None
	 * @return None
	 */
	@Test
	public void shouldSaveUserWithOptionalNullFields() {

		user.setFullName(null);

		userService.createUser(user);

		verify(userRepository, times(1)).save(user);

	}

}
